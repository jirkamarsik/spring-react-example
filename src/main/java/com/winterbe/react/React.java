package com.winterbe.react;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class React {

    private ThreadLocal<Context> engineHolder = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            Context context = Context.newBuilder("js")
                    .option("inspect", "4242")
                    .option("inspect.Path", "debugging-react")
                    .build();
            try {
                for (String path : new String[]{"static/nashorn-polyfill.js", "static/vendor/react.js", "static/vendor/showdown.min.js", "static/commentBox.js"}) {
                    Source src = Source.newBuilder("js", read(path), path).build();
                    context.eval(src);
                }
            } catch (PolyglotException | IOException e) {
                throw new RuntimeException(e);
            }
            return context;
        }
    };

    public  String renderCommentBox(List<Comment> comments) {
        try {
            Object html = engineHolder.get().eval("js", "renderServer").execute(comments);
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