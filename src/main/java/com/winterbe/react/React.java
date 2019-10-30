package com.winterbe.react;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class React {

    private ThreadLocal<Context> contextHolder = ThreadLocal.withInitial(() -> {
            Context context = Context.newBuilder("js").allowHostAccess(true).allowExperimentalOptions(true).option("js.nashorn-compat", "true").build();
            try {
                for (String path : new String[]{"static/vendor/react.js", "static/vendor/showdown.min.js", "static/commentBox.js"}) {
                    Source src = Source.newBuilder("js", read(path), path).build();
                    context.eval(src);
                }
            } catch (PolyglotException | IOException e) {
                throw new RuntimeException(e);
            }
            return context;
    });

    public String renderCommentBox(List<Comment> comments) {
        try {
            Value html = contextHolder.get().getBindings("js").getMember("renderServer").execute(comments);
            return html.toString();
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
