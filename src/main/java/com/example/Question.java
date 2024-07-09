package com.example;

import java.util.List;

class Question {
    String id;
    String value;
    Option options;
    String answer;

    Question(String id, String value, Option options, String answer) {
        this.id = id;
        this.value = value;
        this.options = options;
        this.answer = answer;
    }
}
