package it.gend.domain;

import javax.xml.crypto.Data;
import java.util.Date;

/**
 * @author Daniele Asteggiante
 */
public class RecordDDL {
    Date eventDate;
    String oraSysEvent;
    String oraDictObjOwner;
    String oraDictObjName;
    String oraDictObjType;
    String oraLoginUser;
    String ddlText;

    public RecordDDL(Date eventDate, String oraSysEvent, String oraDictObjOwner, String oraDictObjName, String oraDictObjType, String oraLoginUser, String ddlText) {
        this.eventDate = eventDate;
        this.oraSysEvent = oraSysEvent;
        this.oraDictObjOwner = oraDictObjOwner;
        this.oraDictObjName = oraDictObjName;
        this.oraDictObjType = oraDictObjType;
        this.oraLoginUser = oraLoginUser;
        this.ddlText = ddlText;
    }

    public String getDdlText() {
        return ddlText;
    }

    public String getOraLoginUser() {
        return oraLoginUser;
    }

    public String getOraDictObjType() {
        return oraDictObjType;
    }

    public String getOraDictObjName() {
        return oraDictObjName;
    }

    public String getOraDictObjOwner() {
        return oraDictObjOwner;
    }

    public String getOraSysEvent() {
        return oraSysEvent;
    }

    public Date getEventDate() {
        return eventDate;
    }
}
