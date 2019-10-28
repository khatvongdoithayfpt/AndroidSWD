package builder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * URLBuilder
 */
public class URLBuilder {
    private StringBuilder url;

    public URLBuilder() {
        url = new StringBuilder();
    }

    public URLBuilder create(String link) {
        this.url.append(link);
        return this;
    }

    public URLBuilder setPath(String path) {
        this.url.append("/" + path);
        return this;
    }

    public URLBuilder setParameter(String name, String value) {
        if (url.indexOf("?") == -1) {
            url.append("?" + name + "=" + value);
        } else {
            url.append("&" + name + "=" + value);
        }
        return this;
    }

    public URL getUrl() {
        URL result = null;
        try {
            result = new URL(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

}