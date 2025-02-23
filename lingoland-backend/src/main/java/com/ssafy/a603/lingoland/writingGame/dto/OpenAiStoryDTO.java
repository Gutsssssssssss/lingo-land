package com.ssafy.a603.lingoland.writingGame.dto;

public record OpenAiStoryDTO(String model, Message[] messages, ResponseFormat response_format) {

	public record Message(String role, String content) {
	}

	public record ResponseFormat(String type) {
	}

	public OpenAiStoryDTO(String story) {
		this("gpt-4o-mini", preprocess(story), new ResponseFormat("json_object"));
	}

	private static Message[] preprocess(String story) {
		Message[] messages = new Message[2];
		messages[0] = new Message("system",
			"You are a text analysis and extraction specialist. You are going to help preprocess sentences to generate images by extracting key descriptive elements from the most important sentence and returning them in JSON format. Here is how you will analyze and extract the information:\n"
				+ "\n"
				+ "1. Analyze the given sentences and identify the most important sentence based on descriptive content.\n"
				+ "2. From the selected sentence, extract key elements such as subjects, objects, and settings.\n"
				+ "3. Structure the extracted information into a JSON format with the specified fields, ensuring that nested structures are flattened.\n"
				+ "\n"
				+ "Analyze the following sentences: \"{{SENTENCES}}\"\n"
				+ "\n"
				+ "Return the results in the following JSON format, ensuring that the medium is always set to 'fairytale illustration' and the descriptions are in English. If a specific content field cannot be filled, leave it as an empty string, but **never** omit any key from the structure:\n"
				+ "\n"
				+ "{\n"
				+ "  \"medium\": \"fairytale illustration\",\n"
				+ "  \"character\": \"character description\",\n"
				+ "  \"environment\": \"environment description\",\n"
				+ "  \"lighting\": \"lighting description\",\n"
				+ "  \"color\": \"color description\",\n"
				+ "  \"mood\": \"mood description\",\n"
				+ "  \"composition\": \"composition description\"\n"
				+ "}\n"
				+ "\n"
				+ "**Important**: Make sure to strictly follow the JSON format. Do not include any additional text or explanations outside of the JSON structure.\n"
				+ "\n"
				+ "Take a deep breath and let’s work this out in a step-by-step way to be sure we have the right answer.");
		messages[1] = new Message("user", story);

		return messages;
	}
}
