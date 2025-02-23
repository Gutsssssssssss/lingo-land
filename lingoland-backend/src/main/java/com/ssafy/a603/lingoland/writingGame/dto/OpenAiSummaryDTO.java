package com.ssafy.a603.lingoland.writingGame.dto;

public record OpenAiSummaryDTO(String model, Message[] messages, ResponseFormat response_format) {

	public record Message(String role, String content) {
	}

	public record ResponseFormat(String type) {
	}

	public OpenAiSummaryDTO(String story) {
		this("gpt-4o-mini", preprocess(story), new ResponseFormat("json_object"));
	}

	private static Message[] preprocess(String story) {
		Message[] messages = new Message[2];
		messages[0] = new Message("system",
			"You are a professional fairy tale editor. Your task is to read the provided fairy tale and create an appropriate title and prologue for it. Follow these steps to ensure a high-quality output:\n"
				+ "\n"
				+ "1. Read the entire fairy tale carefully.\n"
				+ "2. Identify the main theme and key elements of the story.\n"
				+ "3. Write a captivating prologue that introduces the story and sets the tone, without giving away too much detail.\n"
				+ "4. Analyze the given sentences and identify the most important sentence based on descriptive content.\n"
				+ "5. From the selected sentence, extract key elements such as subjects, objects, and settings.\n"
				+ "6. Structure the extracted information into a JSON format with the specified fields, ensuring that nested structures are flattened.\n"
				+ "7. Analyze the following fairy tale: \"{{FAIRY_TALE}}\"\n"
				+ "\n"
				+ "Return the results in the following JSON format, ensuring that the \"summary\" field is in Korean, while all other fields are in English. If a specific content field cannot be filled, leave it as an empty string, but **never** omit any key from the structure:\n"
				+ "{\n"
				+ "  \"summary\": \"동화의 프롤로그\",\n"
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
				+ "Take a deep breath and let's work this out in a step-by-step way to be sure we have the right answer.");
		messages[1] = new Message("user", story);

		return messages;
	}
}