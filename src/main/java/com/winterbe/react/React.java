package com.winterbe.react;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class React {

    private ThreadLocal<ScriptEngine> engineHolder = new ThreadLocal<ScriptEngine>() {
        @Override
        protected ScriptEngine initialValue() {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Graal.js");
            try {
                scriptEngine.eval(read("static/nashorn-polyfill.js"));
                scriptEngine.eval(read("static/vendor/react.js"));
                scriptEngine.eval(read("static/vendor/showdown.min.js"));
                scriptEngine.eval(read("static/commentBox.js"));
            } catch (ScriptException e) {
                throw new RuntimeException(e);
            }
            return scriptEngine;
        }
    };

    public  String renderCommentBox(List<Comment> comments) {
        try {
            Object html = ((Invocable)engineHolder.get()).invokeFunction("renderServer", comments);
            return String.valueOf(html);
        }
        catch (Exception e) {
            throw new IllegalStateException("failed to render react component", e);
        }
    }

    private Reader read(String path) {
        InputStream in = getClass().getClassLoader().getResourceAsStream(path);
        return new InputStreamReader(in);
    }
}