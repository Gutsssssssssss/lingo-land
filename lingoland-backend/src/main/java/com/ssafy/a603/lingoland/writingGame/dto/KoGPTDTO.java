package com.ssafy.a603.lingoland.writingGame.dto;

public record KoGPTDTO(String prompt, Integer max_tokens, Double temperature, Double top_p) {
	public KoGPTDTO(String prompt) {
		this(prompt, 32, 0.1,
			1.0);
	}

	private static String preprocess(String prompt) {
		prompt = prompt.trim();
		return "Prompt\n"
			+ "User Persona: \"Elementary Student Writing Assistant\"\n"
			+ "User Goal: \"Preprocess a paragraph written by an elementary student into English keywords before converting it to a drawing\"\n"
			+ "User Task: \"Extract important keywords from a Korean paragraph and output them in English\"\n"
			+ "\n"
			+ "GPT Persona: \"Language Processing Expert\"\n"
			+ "\n"
			+ "Prompt:\n"
			+ "You are a Language Processing Expert. Extract the important keywords from a paragraph written by an elementary student in Korean and translate them into English.\n"
			+ "\n"
			+ "Here is an example of how you will perform the task:\n"
			+ "<task>\n"
			+ "Original Paragraph (Korean):\n"
			+ "\"{{USER_PARAGRAPH}}\"\n"
			+ "Keyword Extraction and Translation (English):\n"
			+ "Extract the important keywords from the above paragraph and translate them into English: \"{{KEYWORDS}}\"\n"
			+ "</task>\n"
			+ "\n"
			+ "Instructions:\n"
			+ "Answer only \"{{keywords}}\".\n"
			+ "\n"
			+ "Now, proceed with the following paragraph:\n"
			+ "Original Paragraph: \"{{USER_PARAGRAPH}}\"\n"
			+ "\n"
			+ "Take a deep breath and let's work this out in a step by step way to be sure we have the right answer.\n"
			+ "\n"
			+ "Template\n"
			+ "Variables:\n"
			+ "{{USER_PARAGRAPH}}: The Korean paragraph written by the elementary student\n"
			+ "{{KEYWORDS}}: The extracted English keywords\n\n"
			+ prompt;
	}
}
