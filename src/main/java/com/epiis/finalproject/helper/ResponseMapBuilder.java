package com.epiis.finalproject.helper;

import com.epiis.finalproject.generic.ResponseGeneric;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to avoid repeated Map-building boilerplate in Business classes.
 * Replaces the pattern:
 * <pre>
 *   Map&lt;String, Object&gt; res = new HashMap&lt;&gt;();
 *   response.success();
 *   response.getListMessage().add("...");
 *   res.put("message", response);
 *   res.put("data", data);
 * </pre>
 */
public final class ResponseMapBuilder {

    private ResponseMapBuilder() {
        // utility class
    }

    /**
     * Marks the response as successful, adds the given message, and returns a map
     * with "message" and "data" entries.
     *
     * @param response the generic response object to mutate
     * @param message  the success message to add
     * @param data     the payload to include under the "data" key
     * @return a {@link Map} containing "message" and "data"
     */
    public static Map<String, Object> buildDataMap(ResponseGeneric response, String message, Object data) {
        response.success();
        response.getListMessage().add(message);

        Map<String, Object> res = new HashMap<>();
        res.put("message", response);
        res.put("data", data);
        return res;
    }
}
