package com.ssafy.a603.lingoland.writingGame.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingoland.fairyTale.entity.FairyTale;
import com.ssafy.a603.lingoland.global.error.entity.ErrorCode;
import com.ssafy.a603.lingoland.global.error.exception.BaseException;
import com.ssafy.a603.lingoland.global.error.exception.InvalidInputException;
import com.ssafy.a603.lingoland.member.security.CustomUserDetails;
import com.ssafy.a603.lingoland.room.service.RoomService;
import com.ssafy.a603.lingoland.util.ImgUtils;
import com.ssafy.a603.lingoland.writingGame.dto.DrawingRequestDTO;
import com.ssafy.a603.lingoland.writingGame.dto.KarloDTO;
import com.ssafy.a603.lingoland.writingGame.dto.KarloReturn;
import com.ssafy.a603.lingoland.writingGame.dto.OllamaStoryDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OllamaStoryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OllamaSummaryDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OllamaSummaryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OpenAiStoryDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OpenAiStoryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OpenAiSummaryDTO;
import com.ssafy.a603.lingoland.writingGame.dto.OpenAiSummaryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.SessionInfo;
import com.ssafy.a603.lingoland.writingGame.dto.SubmitStoryResponseDTO;
import com.ssafy.a603.lingoland.writingGame.dto.WritingGameStartRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WritingGameServiceImpl implements WritingGameService {
	private static final String FAIRY_TALE_IMAGE_PATH = "fairyTale";
	private final ObjectMapper objectMapper;
	private final RedisTemplate<String, Object> redisTemplate;
	private final RestClient restClient;
	private final ImgUtils imgUtils;
	private final RoomService roomService;

	@Qualifier("myExecutor")
	private final ExecutorService executor;

	@Value("${kakao.api.key}")
	private String imageApiKey;

	@Value("${ollama.api-url}")
	private String ollamaUrl;

	@Value("${openai.api.key}")
	private String chatpgtKey;

	@Override
	@Transactional
	public void start(String sessionId, WritingGameStartRequestDTO request) {
		log.info("Starting game for session: {}", sessionId);
		String redisKey = makeRedisRoomKey(sessionId);
		SessionInfo sessionInfo = new SessionInfo(request.maxTurn());
		serializeToRedis(redisKey, sessionInfo);
		log.debug("Session info serialized to Redis with key: {}", redisKey);
	}

	@Override
	@Transactional
	public void setTitle(String sessionId, CustomUserDetails customUserDetails, String title) {
		log.info("Setting title for session: {} by user: {}", sessionId, customUserDetails.getUsername());
		String redisRoomKey = makeRedisRoomKey(sessionId);
		SessionInfo sessionInfo = deserializeFromRedis(redisRoomKey, new TypeReference<SessionInfo>() {
		});
		sessionInfo.addParticipant(customUserDetails.getUsername());
		serializeToRedis(redisRoomKey, sessionInfo);

		log.debug("Participant added to session info: {}", sessionInfo.getParticipants());

		String redisAliveKey = makeRedisMemberAliveKey(sessionId, customUserDetails.getUsername());
		serializeToRedis(redisAliveKey, true);
		String redisSubmitKey = makeRedisMemberSubmitKey(sessionId, customUserDetails.getUsername());
		serializeToRedis(redisSubmitKey, false);

		roomService.create(sessionId, customUserDetails, title);
		log.debug("Room created and title set: {}", title);
	}

	@Override
	@Transactional
	public SubmitStoryResponseDTO submitStory(String sessionId, DrawingRequestDTO request,
		CustomUserDetails customUserDetails) {
		log.info("Submitting story for session: {} by user: {}", sessionId, customUserDetails.getUsername());
		String redisSubmitKey = makeRedisMemberSubmitKey(sessionId, customUserDetails.getUsername());
		serializeToRedis(redisSubmitKey, true);

		// 비동기적으로 이미지 생성 실행
		CompletableFuture.runAsync(() -> processSingleStory(sessionId, request, customUserDetails), executor);

		String redisRoomKey = makeRedisRoomKey(sessionId);
		SessionInfo sessionInfo = deserializeFromRedis(redisRoomKey, new TypeReference<SessionInfo>() {
		});
		List<FairyTale> fairyTales;
		if (sessionInfo.getMaxTurn() == request.order()) {
			log.info("Last turn for session: {}", sessionId);

			fairyTales = roomService.findFairyTalesInSession(sessionId);

			CompletableFuture.runAsync(() -> endProcess(sessionId, request, customUserDetails), executor)
				.thenRunAsync(() -> {
					redisTemplate.delete(makeRedisMemberAliveKey(sessionId, customUserDetails.getUsername()));
					redisTemplate.delete(makeRedisMemberSubmitKey(sessionId, customUserDetails.getUsername()));
					roomService.endRoom(sessionId, request.key());
					log.info("Ended room and cleaned up session data for session: {}", sessionId);
				}, executor);
		} else {
			fairyTales = null;
		}

		if (isAllEnd(sessionId, sessionInfo)) {
			for (String participant : sessionInfo.getParticipants()) {
				redisSubmitKey = makeRedisMemberSubmitKey(sessionId, participant);
				serializeToRedis(redisSubmitKey, false);
			}
			log.info("All participants have ended their submissions for session: {}", sessionId);
			return SubmitStoryResponseDTO.builder()
				.fairyTales(fairyTales)
				.goNext(true)
				.build();
		}
		return SubmitStoryResponseDTO.builder()
			.fairyTales(fairyTales)
			.goNext(false)
			.build();
	}

	@Override
	@Transactional
	public Boolean exit(String sessionId, String exitLoginId, Integer order) {
		log.info("User {} exiting session: {}", exitLoginId, sessionId);
		String redisAliveKey = makeRedisMemberAliveKey(sessionId, exitLoginId);
		serializeToRedis(redisAliveKey, false);
		String redisRoomKey = makeRedisRoomKey(sessionId);
		SessionInfo sessionInfo = deserializeFromRedis(redisRoomKey, new TypeReference<SessionInfo>() {
		});
		log.debug("Session info after user exit: {}", sessionInfo);
		// if (order != sessionInfo.getMaxTurn()) {
		// 	String redisSubmitKey = makeRedisMemberSubmitKey(sessionId, exitLoginId);
		// 	Boolean isSubmit = deserializeFromRedis(redisSubmitKey, new TypeReference<Boolean>() {
		// 	});
		// 	if (!isSubmit) {
		// 		roomService.fairytaleInComplete(sessionId, exitLoginId);
		// 	}
		// }
		return isAllEnd(sessionId, sessionInfo);
	}

	protected void processSingleStory(String sessionId, DrawingRequestDTO request,
		CustomUserDetails customUserDetails) {
		log.info("Processing single story: {} for session: {}", request.key(), sessionId);
		String story = request.story();
		String imgUrl = handleStoryTranslation(story);
		String savedImgUrl = null;
		if (imgUrl.equals(imgUtils.getDefaultImage()))
			savedImgUrl = imgUtils.getImagePathWithDefaultImage(FAIRY_TALE_IMAGE_PATH);
		else
			savedImgUrl = imgUtils.saveBase64Image(imgUrl, FAIRY_TALE_IMAGE_PATH);
		log.debug("Story image saved at: {}", savedImgUrl);
		saveStory(sessionId, request, customUserDetails, savedImgUrl);
	}

	private void endProcess(String sessionId, DrawingRequestDTO request, CustomUserDetails customUserDetails) {
		log.info("Ending process for session: {} with request: {}", sessionId, request.key());
		FairyTale curFairyTale = roomService.findFairyTale(sessionId, customUserDetails.getUsername());
		String stories = curFairyTale.getContent().stream()
			.map(FairyTale.Story::getStory)
			.collect(Collectors.joining("\n"));

		String imgUrl;
		OpenAiSummaryResponseDTO imagePrompt = null;
		try {
			imagePrompt = makeTitleWithSummary2(stories);
		} catch (Exception e) {
			log.warn("Skipping image generation due to title creation failure.");
			imgUrl = imgUtils.getDefaultImage();
		}

		if (imagePrompt == null || imagePrompt.summary() == null) {
			imgUrl = imgUtils.getDefaultImage();
		} else {
			imgUrl = generateImageWithFallback(imagePrompt.toPrompt().toString());
		}

		String savedImgUrl;
		if (imgUrl.equals(imgUtils.getDefaultImage()))
			savedImgUrl = imgUtils.getImagePathWithDefaultImage(FAIRY_TALE_IMAGE_PATH);
		else
			savedImgUrl = imgUtils.saveBase64Image(imgUrl, FAIRY_TALE_IMAGE_PATH);
		roomService.fairytaleComplete(sessionId, request.key(), savedImgUrl, imagePrompt.summary());
		log.info("Completed fairy tale for session: {} with cover image: {}", sessionId, savedImgUrl);
	}

	private String handleStoryTranslation(String story) {
		log.info("Translating story to image prompt");
		OpenAiStoryResponseDTO translated = null;
		try {
			translated = translateStory2ImagePrompt2(story); // GPT API 활용하여 번역
		} catch (Exception e) {
			log.warn("Skipping image generation due to translation failure.");
			return imgUtils.getDefaultImage();
		}
		if (translated == null || translated.medium().isBlank()) {
			log.warn("Skipping image generation due to empty translation.");
			return imgUtils.getDefaultImage();
		}
		return generateImageWithFallback(translated.toString()); // 변환된 프롬프트를 Karlo API에 전달
	}

	private String generateImageWithFallback(String translated) {
		log.info("Generating image with fallback for translated text.");
		try {
			return generateImage(translated);
		} catch (Exception e) {
			log.error("Image generation failed, using default image", e);
			return imgUtils.getDefaultImage();
		}
	}

	private void saveStory(String sessionId, DrawingRequestDTO request, CustomUserDetails customUserDetails,
		String imgUrl) {
		log.info("Saving story for session: {} with image URL: {}", sessionId, imgUrl);
		roomService.fairytaleStoryAdd(sessionId, request.key(), customUserDetails,
			new FairyTale.Story(imgUrl, request.story()));
	}

	private OllamaSummaryResponseDTO makeTitleWithSummary(String story) {
		log.info("Creating summary with llama3.1 for story: {}", story);
		String json = restClient.post()
			.uri(ollamaUrl)
			.body(new OllamaSummaryDTO(story))
			.retrieve()
			.body(String.class);
		try {
			JsonNode jsonNode = objectMapper.readTree(json);
			String response = jsonNode.get("response").asText().trim();
			log.info("Summary result: {}", response);
			return objectMapper.readValue(response, new TypeReference<OllamaSummaryResponseDTO>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Failed to process JSON response from translation API", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
	}

	private OllamaStoryResponseDTO translateStory2ImagePrompt(String story) {
		log.info("Translating story using llama3.1: {}", story);
		String json = restClient.post()
			.uri(ollamaUrl)
			.body(new OllamaStoryDTO(story))
			.retrieve()
			.body(String.class);
		try {
			JsonNode jsonNode = objectMapper.readTree(json);
			String response = jsonNode.get("response").asText();
			log.info("Translation result: {}", response);
			return objectMapper.readValue(response, new TypeReference<OllamaStoryResponseDTO>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Failed to process JSON response from translation API", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
	}

	private OpenAiSummaryResponseDTO makeTitleWithSummary2(String story) {
		log.info("Creating summary with OpenAi for story: {}", story);
		String json = restClient.post()
			.uri("https://api.openai.com/v1/chat/completions")
			.header("Authorization", "Bearer " + chatpgtKey)
			.body(new OpenAiSummaryDTO(story))
			.retrieve()
			.body(String.class);
		log.info("response make title with summary : {}", json);
		try {
			JsonNode jsonNode = objectMapper.readTree(json);
			JsonNode choiceNode = jsonNode.path("choices");
			JsonNode messageNode = choiceNode.get(0).path("message");
			String content = messageNode.path("content").asText().trim();
			log.info("Summary result: {}", content);
			return objectMapper.readValue(content, new TypeReference<OpenAiSummaryResponseDTO>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Failed to process JSON response from translation API", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
		// return json;
	}

	private OpenAiStoryResponseDTO translateStory2ImagePrompt2(String story) {
		log.info("Translating story using OpenAI: {}", story);
		String json = restClient.post()
			.uri("https://api.openai.com/v1/chat/completions")
			.header("Authorization", "Bearer " + chatpgtKey)
			.body(new OpenAiStoryDTO(story))
			.retrieve()
			.body(String.class);
		log.info("response story : {}", json);
		try {
			JsonNode jsonNode = objectMapper.readTree(json);
			JsonNode choiceNode = jsonNode.path("choices");
			JsonNode messageNode = choiceNode.get(0).path("message");
			String content = messageNode.path("content").asText().trim();
			log.info("Translation result: {}", content);
			return objectMapper.readValue(content, new TypeReference<OpenAiStoryResponseDTO>() {
			});
		} catch (JsonProcessingException e) {
			log.error("Failed to process JSON response from translation API", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
		// return json;
	}

	private String generateImage(String translated) {
		log.info("Generating image using text: {}", translated);
		KarloReturn karloReturn = restClient.post()
			.uri("https://api.kakaobrain.com/v2/inference/karlo/t2i")
			.header("Authorization", "KakaoAK " + imageApiKey)
			.header("Content-Type", "application/json")
			.body(new KarloDTO(translated))
			.retrieve()
			.body(KarloReturn.class);

		if (karloReturn != null && karloReturn.images().length > 0) {
			log.info("Image seed: {}", karloReturn.images()[0].seed());
			return karloReturn.images()[0].image();
		}
		log.error("Failed to generate image for translated text: {}", translated);
		throw new InvalidInputException(ErrorCode.INVALID_INPUT_VALUE);
	}

	private <T> void serializeToRedis(String key, T value) {
		try {
			redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), 60, TimeUnit.MINUTES);
			log.debug("Serialized object to Redis with key: {}", key);
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize object to Redis", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
	}

	private <T> T deserializeFromRedis(String key, TypeReference<T> valueTypeRef) {
		String json = (String)redisTemplate.opsForValue().get(key);
		try {
			T result = objectMapper.readValue(json, valueTypeRef);
			log.debug("Deserialized object from Redis with key: {}", key);
			return result;
		} catch (JsonProcessingException e) {
			log.error("Failed to deserialize object from Redis", e);
			throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
		}
	}

	private boolean isAllEnd(String sessionId, SessionInfo sessionInfo) {
		log.info("Checking if all participants have ended for session: {}", sessionId);
		String lockKey = "lock:" + sessionId + ":isAllEnd";
		String redisAliveKey;
		String redisSubmitKey;
		int count = 0;

		// Acquire a distributed lock
		Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", Duration.ofSeconds(5));
		if (Boolean.TRUE.equals(acquired)) {
			try {
				for (String participant : sessionInfo.getParticipants()) {
					redisAliveKey = makeRedisMemberAliveKey(sessionId, participant);
					redisSubmitKey = makeRedisMemberSubmitKey(sessionId, participant);

					Boolean isAlive = deserializeFromRedis(redisAliveKey, new TypeReference<Boolean>() {
					});
					Boolean isSubmit = deserializeFromRedis(redisSubmitKey, new TypeReference<Boolean>() {
					});

					if (Boolean.FALSE.equals(isAlive) || Boolean.TRUE.equals(isSubmit)) {
						count++;
					}
				}
			} finally {
				// Release the lock
				redisTemplate.delete(lockKey);
			}
		} else {
			// Could not acquire lock, return false or retry after some time
			log.warn("Could not acquire lock for session: {}", sessionId);
			return false;
		}

		boolean allEnded = count == sessionInfo.getParticipants().size();
		log.debug("All participants ended: {}", allEnded);
		return allEnded;
	}

	private String makeRedisRoomKey(String sessionId) {
		return "lingoland:fairyTale:session:" + sessionId;
	}

	private String makeRedisMemberAliveKey(String sessionId, String loginId) {
		return "lingoland:fairyTale:session:" + sessionId + ":member:" + loginId + ":alive";
	}

	private String makeRedisMemberSubmitKey(String sessionId, String loginId) {
		return "lingoland:fairyTale:session:" + sessionId + ":member:" + loginId + ":submit";
	}

	private void deleteKeysByPattern(String pattern) {
		log.info("Deleting keys with pattern: {}", pattern);
		Set<String> keySet = redisTemplate.keys(pattern);
		redisTemplate.delete(keySet);
		log.debug("Deleted keys: {}", keySet);
	}

	// @Async("asyncExecutor")
	// protected CompletableFuture<Void> processStoriesAsync(String sessionId, List<DrawingRequestDTO> requests,
	// 	WritingGameStartRequestDTO sessionInfo) {
	// 	log.info("Processing stories asynchronously for session: {}", sessionId);
	// 	List<CompletableFuture<Void>> futures = new ArrayList<>();
	// 	for (DrawingRequestDTO request : requests) {
	// 		log.debug("Processing single story for session: {}, order: {}", sessionId, request.order());
	// 		futures.add(CompletableFuture.runAsync(() -> processSingleStory(sessionId, request), executor));
	// 	}
	// 	return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
	// 		.thenRun(() -> log.info("All story processing tasks completed for session: {}", sessionId));
	// }
	// private void saveStoryToRedis(String key, DrawingRequestDTO request, String savedImgUrl) {
	// 	log.info("Saving story to Redis with key: {}", key);
	// 	String redisStoryKey = "lingoland:fairyTale:" + key;
	// 	FairyTale.Story node = FairyTale.Story.builder()
	// 		.illustration(savedImgUrl)
	// 		.story(request.story())
	// 		.build();
	//
	// 	try {
	// 		if (request.order() == 1) {
	// 			List<FairyTale.Story> stories = new ArrayList<>();
	// 			stories.add(node);
	// 			serializeToRedis(redisStoryKey, stories);
	// 			log.debug("Serialized new story to Redis for key: {}", redisStoryKey);
	// 			return;
	// 		}
	// 		List<FairyTale.Story> existingStories = getExistingStories(redisStoryKey);
	// 		existingStories.add(node);
	// 		serializeToRedis(redisStoryKey, existingStories);
	// 		log.debug("Serialized updated story to Redis for key: {}", redisStoryKey);
	// 	} catch (JsonProcessingException e) {
	// 		log.error("Error processing JSON for Redis serialization", e);
	// 		throw new BaseException(ErrorCode.JSON_PROCESSING_FAILED);
	// 	}
	// }
	//
	// private List<FairyTale.Story> getExistingStories(String redisStoryKey) throws JsonProcessingException {
	// 	String existingJson = (String)redisTemplate.opsForValue().get(redisStoryKey);
	// 	return existingJson != null ? objectMapper.readValue(existingJson, new TypeReference<List<FairyTale.Story>>() {
	// 	}) : new ArrayList<>();
	// }
	// @Async("asyncExecutor")
	// protected List<FairyTale> end(List<DrawingRequestDTO> requests) {
	// 	log.info("Ending game session with {} requests", requests.size());
	//
	// 	List<String> writers = requests.stream()
	// 		.map(DrawingRequestDTO::key)
	// 		.collect(Collectors.toList());
	// 	log.debug("Writers involved in this session: {}", writers);
	//
	// 	List<CompletableFuture<FairyTale>> futures = requests.stream()
	// 		.map(request -> CompletableFuture.supplyAsync(() -> {
	// 				log.info("Processing end of session for request: {}", request.key());
	// 				return endProcess(request, writers);
	// 			}, executor)
	// 			.exceptionally(ex -> {
	// 				log.error("Error processing request: {}", request.key(), ex);
	// 				throw new InvalidInputException(ErrorCode.INTERNAL_SERVER_ERROR);
	// 			}))
	// 		.collect(Collectors.toList());
	//
	// 	log.info("Waiting for all end-process tasks to complete");
	// 	CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	//
	// 	List<FairyTale> fairyTales = futures.stream()
	// 		.map(CompletableFuture::join)
	// 		.collect(Collectors.toList());
	// 	log.info("Completed end-process tasks for all requests");
	//
	// 	return fairyTales;
	// }
}