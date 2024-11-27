package it.gend.domain;

import java.util.Date;

/**
 * @author Daniele Asteggiante
 */
public class ScriptVersion {
    String software;
    String softwareVersion;
    Date scriptDate;
    String content;

    public ScriptVersion(String software, String softwareVersion, Date scriptDate, String content) {
        this.software = software;
        this.softwareVersion = softwareVersion;
        this.scriptDate = scriptDate;
        this.content = content;
    }

    public String getSoftware() {
        return software;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public Date getScriptDate() {
        return scriptDate;
    }

    public String getContent() {
        return content;
    }
}
