package com.ssafy.a603.lingolandbatch.problem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.a603.lingolandbatch.config.BatchConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
//@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService{

    private final ProblemRepository problemRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Qualifier("myExecutor")
    private final ExecutorService executor;

    private final BlockingQueue<Problem> buffer;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long t = 200;

    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static int maxThreads = 0;

    @Value("${spring.chatGPTKey}")
    String apiKey;

    private int cnt = 0;
    @Value("${spring.executionCnt}")
    private int executionCnt;
    @Value("${spring.taskletCnt}")
    private int taskletCnt;
    private int maxCnt;

    private ProblemServiceImpl(ProblemRepository repository, WebClient.Builder webClient, ObjectMapper objectMapper, ExecutorService executor){
        this.problemRepository = repository;
        this.webClient = webClient
                .build();
        this.objectMapper = objectMapper;
        this.executor = executor;
        this.buffer = new LinkedBlockingDeque<>();
    }

    public void start(){
        log.info("current time delay : {}", t);
        maxCnt = executionCnt * taskletCnt;
        AtomicReference<Long> delay = new AtomicReference<>(t);
        scheduler.schedule(() -> slowStart(delay), delay.get(), TimeUnit.MICROSECONDS);
    }

    private void slowStart(AtomicReference<Long> delay){
        if(maxCnt <= cnt && buffer.isEmpty()) {
            log.info("&&&&& scheduler terminate");
            scheduler.shutdown();
        }

        List<Problem> problems = new ArrayList<>();
        buffer.drainTo(problems, 5);

        if(!problems.isEmpty()){
            boolean isSaved = saveList(problems);

            if(isSaved){
                log.info("saved {}", problems.size());
                delay.set((long) (0.9 * delay.get()));
            } else {
                log.info("save failed");
                delay.set((long) 2 * delay.get());
            }
        } else {
            delay.set((long) 200);
        }

        log.info("&&&&& time delay {}", delay.get());
        scheduler.schedule(() -> slowStart(delay), delay.get(), TimeUnit.MICROSECONDS);
    }

    private boolean saveList(List<Problem> problems){
        try {
            problemRepository.saveAll(problems);
            return true;
        } catch (Exception e){
            return false;
        }
    }

// webflux
    @Override
    public void makeProblem() {

        webClient.post()
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue("{\n" +
                        "    \"model\": \"gpt-4o-mini\",\n" +
                        "    \"messages\": [\n" +
                        "      {\n" +
                        "        \"role\" : \"system\",\n" +
                        "        \"content\" : \"상황 : -사용자는 어휘력, 문법 능력을 향상시키고자 함. -사용자는 다양한 수준의 문제를 경험하고자 함. 입력값 : -사용자가 관심이 있는 특정 영역이나 주제 지시사항:-사용자의 관심 분야에 따라 관련 문제 생성-학습을 강화하기 위한 상호 작용형 퀴즈나 연습문제 제공-문제를 생성하고 다시 검토하기-문제는 선택지를 3개 제공한다.-정답은 반드시 1개이고 오답은 반드시 2개이다.-문제는 json 형식이다.-json내에 들어가는 문제 내용을 제외한 어떤 내용도 대답에 포함하지 마라.-문법 문제 2개, 어휘력 문제 3개, 문해력 문제 3개, 높임법 문제 2개를 내어라.가이드라인 :쉬운 어휘 사용출력 지시사항:json 형식{\\\"problem\\\": 문제제시,\\\"1\\\": 선택지 1,\\\"2\\\": 선택지 2,\\\"3\\\" : 선택지 3,\\\"answer\\\" : 답,“explanation”:”해설”}조건:-대한민국 초등학교 국어 교사가 사용한다.-시험문제를 만드는 상황이다.-문해력 저하에 대해 문제를 느끼고 있어 관련 문제를 제공한다.-난이도는 상,중,하 로 나누고 어려운 문제는 빈도를 낮추고 배치를 뒤로 한다.-문제를 생성할 때에 계획을 하고 문제를 만들기 위해 계획을 하나하나 실행하세요.-문제는 3개의 선택지로 제공한다.-정답은 무조건 1개이다.-문제를 생성할 때에는 모든 선택지를 검토한다.-문제를 생성할 때에는 출처가 있어야 한다.-문제 형식은 예시를 참고해야 한다.문제의 길이가 한 문장을 초과할 경우 문장마다 개행 문자를 삽입하라.-모든 문제는 'problems' 배열 내에 들어갸야 한다.\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }")
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.info("{}", body);
                                    BatchConfig.setExecutionCntWhenError();
                                    return Mono.error(new WebClientResponseException(
                                            response.statusCode().value(),
                                            "HTTP Error: " + response.statusCode(),
                                            response.headers().asHttpHeaders(),
                                            null,
                                            null
                                    ));
                                })
                )
                .bodyToMono(String.class)
                .subscribe(
                        messageBody -> {
                            log.info("request success");
                            log.info("{}",messageBody);

                            try {
                                JsonNode jsonNode = objectMapper.readTree(messageBody);
                                JsonNode choiceNode = jsonNode.path("choices");
                                JsonNode messageNode = choiceNode.get(0).path("message");
                                String content = messageNode.path("content").asText();
                                String jsonString = content.replaceAll("```json\\n", "").replaceAll("\\n```", "");
                                JsonNode contentNode = objectMapper.readTree(jsonString);
                                JsonNode problemsNode = contentNode.path("problems");

                                JsonNode usageNode = jsonNode.path("usage");
                                int totalToken = usageNode.path("total_tokens").asInt();

                                log.info("total token spent {}", totalToken);
                                for(JsonNode problemNode : problemsNode){

                                    List<Problem.Detail.Choice> choices = new ArrayList<>();
                                    for(int j = 0; j < 3; j++) {
                                        choices.add(
                                                new Problem.Detail.Choice(
                                                        j + 1,
                                                        problemNode.path(String.valueOf(j + 1)).asText())
                                        );
                                    }

                                    Problem.Detail detail = new Problem.Detail(
                                            problemNode.path("problem").asText(),
                                            choices,
                                            problemNode.path("answer").asInt(),
                                            problemNode.path("explanation").asText()
                                    );

                                    Problem problem = Problem.builder()
                                            .correctAnswerCount(0L)
                                            .incorrectAnswerCount(0L)
                                            .deletedAt(null)
                                            .isDeleted(false)
                                            .inspector(null)
                                            .creator("chatGPT")
                                            .detail(detail)
                                            .build();

//                                    problemRepository.save(problem);
                                    buffer.put(problem);
                                    log.info("문제 저장: {}", problem);
                                }
                            } catch (JsonProcessingException | InterruptedException e) {
                                throw new RuntimeException(e);
                            } finally {
                                log.info("request : {}, time : {}", cnt,
                                        System.currentTimeMillis() - BatchConfig.startTime);
                                cnt++;
                                maxThreads = Math.max(maxThreads, threadMXBean.getThreadCount());
                                log.info("maxThreads: {}", maxThreads);

                                long[] threadIds = threadMXBean.getAllThreadIds();

                                int virtualThreadCount = 0;

                                // 각 스레드를 순회하면서 가상 스레드인지 확인
                                for (long threadId : threadIds) {
                                    Thread thread = findThreadById(threadId);

                                    if (thread != null && thread.isVirtual()) {
                                        virtualThreadCount++;
                                    }
                                }

                                log.info("&&&virtualThreadCount: {}", virtualThreadCount);
                            }

                            log.info("store success");
                        },
                        error -> {
                            log.info("request failed {}", error.getMessage());
                        }
                );
    }
    public  void makeProblemRestTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        headers.setBearerAuth(apiKey);

        // 요청 바디 설정
        String requestBody = "{\n" +
                "    \"model\": \"gpt-4o-mini\",\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"role\" : \"system\",\n" +
                "        \"content\" : \"상황 : -사용자는 어휘력, 문법 능력을 향상시키고자 함. -사용자는 다양한 수준의 문제를 경험하고자 함. 입력값 : -사용자가 관심이 있는 특정 영역이나 주제 지시사항:-사용자의 관심 분야에 따라 관련 문제 생성-학습을 강화하기 위한 상호 작용형 퀴즈나 연습문제 제공-문제를 생성하고 다시 검토하기-문제는 선택지를 3개 제공한다.-정답은 반드시 1개이고 오답은 반드시 2개이다.-문제는 json 형식이다.-json내에 들어가는 문제 내용을 제외한 어떤 내용도 대답에 포함하지 마라.-문법 문제 1개, 어휘력 문제 2개, 문해력 문제 1개, 높임법 문제 1개를 내어라.가이드라인 :쉬운 어휘 사용출력 지시사항:json 형식{\\\"problem\\\": 문제제시,\\\"1\\\": 선택지 1,\\\"2\\\": 선택지 2,\\\"3\\\" : 선택지 3,\\\"answer\\\" : 답,“explanation”:”해설”}조건:-대한민국 초등학교 국어 교사가 사용한다.-시험문제를 만드는 상황이다.-문해력 저하에 대해 문제를 느끼고 있어 관련 문제를 제공한다.-난이도는 상,중,하 로 나누고 어려운 문제는 빈도를 낮추고 배치를 뒤로 한다.-문제를 생성할 때에 계획을 하고 문제를 만들기 위해 계획을 하나하나 실행하세요.-문제는 3개의 선택지로 제공한다.-정답은 무조건 1개이다.-문제를 생성할 때에는 모든 선택지를 검토한다.-문제를 생성할 때에는 출처가 있어야 한다.-문제 형식은 예시를 참고해야 한다.문제의 길이가 한 문장을 초과할 경우 문장마다 개행 문자를 삽입하라.-모든 문제는 'problems' 배열 내에 들어갸야 한다.\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";
        // HttpEntity에 헤더와 바디 설정
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST 요청 보내기
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        String messageBody = requestEntity.getBody();
        // 응답 출력
        System.out.println(messageBody);

        // code to parse response to store
//        try {
//            JsonNode jsonNode = objectMapper.readTree(messageBody);
//            JsonNode choiceNode = jsonNode.path("choices");
//            JsonNode messageNode = choiceNode.get(0).path("message");
//            String content = messageNode.path("content").asText();
//            String jsonString = content.replaceAll("```json\\n", "").replaceAll("\\n```", "");
//            JsonNode contentNode = objectMapper.readTree(jsonString);
//            JsonNode problemsNode = contentNode.path("problems");
//
//            JsonNode usageNode = jsonNode.path("usage");
//            int totalToken = usageNode.path("total_tokens").asInt();
//
//            log.info("total token spent {}", totalToken);
//            for(JsonNode problemNode : problemsNode){
//
                List<Problem.Detail.Choice> choices = new ArrayList<>();
                for(int j = 0; j < 3; j++) {
                    choices.add(
                            new Problem.Detail.Choice(
                                    j + 1,
                                    "1")
                    );
                }
//
                Problem.Detail detail = new Problem.Detail(
                        "problem",
                        choices,
                        1,
                        "explanation"
                );
//
                Problem problem = Problem.builder()
                        .correctAnswerCount(0L)
                        .incorrectAnswerCount(0L)
                        .deletedAt(null)
                        .isDeleted(false)
                        .inspector(null)
                        .creator("chatGPT")
                        .detail(detail)
                        .build();



                problemRepository.save(problem);
                log.info("문제 저장: {}", problem);
//            }
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } finally {
            log.info("request : {}, time : {}", cnt,
                    System.currentTimeMillis() - BatchConfig.startTime);
//        }
        cnt++;
        log.info("store success");
    };

    // completableFuture and virtual thread
    public  void makeProblemVirtualThread(){

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
        headers.setBearerAuth(apiKey);

        // 요청 바디 설정
        String requestBody = "{\n" +
                "    \"model\": \"gpt-4o-mini\",\n" +
                "    \"messages\": [\n" +
                "      {\n" +
                "        \"role\" : \"system\",\n" +
                "        \"content\" : \"상황 : -사용자는 어휘력, 문법 능력을 향상시키고자 함. -사용자는 다양한 수준의 문제를 경험하고자 함. 입력값 : -사용자가 관심이 있는 특정 영역이나 주제 지시사항:-사용자의 관심 분야에 따라 관련 문제 생성-학습을 강화하기 위한 상호 작용형 퀴즈나 연습문제 제공-문제를 생성하고 다시 검토하기-문제는 선택지를 3개 제공한다.-정답은 반드시 1개이고 오답은 반드시 2개이다.-문제는 json 형식이다.-json내에 들어가는 문제 내용을 제외한 어떤 내용도 대답에 포함하지 마라.-문법 문제 1개, 어휘력 문제 2개, 문해력 문제 1개, 높임법 문제 1개를 내어라.가이드라인 :쉬운 어휘 사용출력 지시사항:json 형식{\\\"problem\\\": 문제제시,\\\"1\\\": 선택지 1,\\\"2\\\": 선택지 2,\\\"3\\\" : 선택지 3,\\\"answer\\\" : 답,“explanation”:”해설”}조건:-대한민국 초등학교 국어 교사가 사용한다.-시험문제를 만드는 상황이다.-문해력 저하에 대해 문제를 느끼고 있어 관련 문제를 제공한다.-난이도는 상,중,하 로 나누고 어려운 문제는 빈도를 낮추고 배치를 뒤로 한다.-문제를 생성할 때에 계획을 하고 문제를 만들기 위해 계획을 하나하나 실행하세요.-문제는 3개의 선택지로 제공한다.-정답은 무조건 1개이다.-문제를 생성할 때에는 모든 선택지를 검토한다.-문제를 생성할 때에는 출처가 있어야 한다.-문제 형식은 예시를 참고해야 한다.문제의 길이가 한 문장을 초과할 경우 문장마다 개행 문자를 삽입하라.-모든 문제는 'problems' 배열 내에 들어갸야 한다.\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }";
        // HttpEntity에 헤더와 바디 설정
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // POST 요청 보내기
        CompletableFuture<ResponseEntity<String>> responseFuture = CompletableFuture.supplyAsync(
                () -> restTemplate.postForEntity(url, requestEntity, String.class), executor);

        responseFuture.thenAccept(response -> {
            String body = response.getBody();
            System.out.println("Response body: " + body);

            List<Problem.Detail.Choice> choices = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                choices.add(
                        new Problem.Detail.Choice(
                                j + 1,
                                "1")
                );
            }

            Problem.Detail detail = new Problem.Detail(
                    "problem",
                    choices,
                    1,
                    "explanation"
            );

            Problem problem = Problem.builder()
                    .correctAnswerCount(0L)
                    .incorrectAnswerCount(0L)
                    .deletedAt(null)
                    .isDeleted(false)
                    .inspector(null)
                    .creator("chatGPT")
                    .detail(detail)
                    .build();



            problemRepository.save(problem);
            log.info("문제 저장: {}", problem);

            log.info("request : {}, time : {}", cnt,
                    System.currentTimeMillis() - BatchConfig.startTime);
            cnt++;
            log.info("store success");

            log.info("maxThreads: {}", maxThreads);

            long[] threadIds = threadMXBean.getAllThreadIds();

            int virtualThreadCount = 0;

            // 각 스레드를 순회하면서 가상 스레드인지 확인
            for (long threadId : threadIds) {
                Thread thread = findThreadById(threadId);

                if (thread != null && thread.isVirtual()) {
                    virtualThreadCount++;
                }
            }

            log.info("&&&virtualThreadCount: {}", virtualThreadCount);
        }).exceptionally(e -> {
            System.err.println("Error: " + e.getMessage());
            return null;
        });
    }

    private static Thread findThreadById(long threadId) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getId() == threadId) {
                return t;
            }
        }
        return null;
    }
}
