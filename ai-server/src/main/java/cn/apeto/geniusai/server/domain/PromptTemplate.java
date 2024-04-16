package cn.apeto.geniusai.server.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apeto
 * @create 2023/8/3 17:11
 */
public interface PromptTemplate {


    String TEXT_SITE = "{text_content}";
    String SUMMARY_SITE = "{summary}";
    String Q_SITE = "{q}";

    /**
     * 严谨
     */
    String RIGOROUS = "\"If you are a very cautious person, for some questions that you are unsure or do not know, you will answer '我不知道' instead of forcing an answer. Now, I will provide you with the following information, sorted from high to low relevance:\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "{text_content}\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "You must only answer what was mentioned in the previous text. If there is an image URL in the previous text, please answer in Chinese Markdown format.";
    /**
     * 宽松
     */

    String LOOSE = "\"If I provide you with the following information, sorted from high to low relevance:\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "{text_content}\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "You must only answer what was mentioned in the previous text. If there is an image URL in the previous text, please answer in Markdown format.";

    String SUMMARY = "我为您提供以下信息，从高到低的相关性排序:\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "{text_content}\n" +
            "\n" +
            "-----------------------\n" +
            "\n" +
            "你必须只回答前面课文中提到的问题. 请总结一下这个文件讲了什么内容？并根据内容总结出三个有代表性的问题.请根据以下模拟输出:\n"+
            "summary:\n" +
            "Q1:\n" +
            "Q2:\n" +
            "Q3:\n";



    static String extractContent(String input, String start, String end) {
        int startIndex = input.indexOf(start);
        if (startIndex == -1) return ""; // start 不存在
        startIndex += start.length();

        int endIndex = end.isEmpty() ? input.length() : input.indexOf(end, startIndex); // 从 startIndex 之后开始查找 end
        if (endIndex == -1 || endIndex < startIndex) return ""; // end 不存在或位置不合理

        return input.substring(startIndex, endIndex).trim();
    }

    static Result extractSummaryAndQuestions(String text) {
        String[] lines = text.split("\n");
        String summary = null;
        List<String> questions = new ArrayList<>();

        Pattern pattern = Pattern.compile("^Q\\d+:\\s*(.+)$");

        for (String line : lines) {
            if (line.startsWith("summary:")) {
                summary = line.replace("summary:", "").trim();
            } else if (line.startsWith("Q")) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    questions.add(matcher.group(1));
                }
            }
        }

        return new Result(summary, questions);
    }

    @Data
    class Result {
        String summary;
        List<String> questions;

        public Result(String summary, List<String> questions) {
            this.summary = summary;
            this.questions = questions;
        }
    }

    public static void main(String[] args) {
        String text = "summary: 这个文件介绍了如何利用GPT4和AI绘画来生成logo设计的方法。对于非专业的图形设计师来说，可以使用GPT4生成设计的描述，然后再利用AI绘画工具进行绘制。\n\nQ1: 如何使用GPT4生成logo设计的描述？\nQ2: AI绘画的文字处理能力相对较弱，应该如何尽量简洁地描述设计要求？\nQ3: 利用AI绘画工具进行logo设计时，应该注意哪些参数和风格要素？";
        Result result = extractSummaryAndQuestions(text);
        System.out.println("Summary: " + result.summary);
        System.out.println("Questions:");
        for (String question : result.questions) {
            System.out.println(question);
        }
    }

}
