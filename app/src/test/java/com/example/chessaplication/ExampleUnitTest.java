package com.example.chessaplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.util.Pair;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import board.Place;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String input = "adbfw47f1fd10";
        Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
        Matcher matcher = lastIntPattern.matcher(input);
        int lastNumberInt = 0;
        String name_without_number = input;
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            name_without_number = input.substring(0, input.length() - someNumberStr.length());
            lastNumberInt = Integer.parseInt(someNumberStr);
        }
        String name = name_without_number+(++lastNumberInt);
        System.out.println(name);
        assertEquals("adbfw47f1fd11",name);

    }
}
