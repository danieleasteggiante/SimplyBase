package it.gend.configuration;

import java.util.Arrays;
import java.util.List;

/**
 * @author Daniele Asteggiante
 */
public interface Constant {
    String recordDLLSeparator = "!L!";
    String elementClassificationSeparator = "!E!";
    String beginClassification = "!C!";
    String endClassification = "!EC!";
    List<ObjType> createOrReplaceble = Arrays.asList(ObjType.VIEW, ObjType.INDEX, ObjType.SEQUENCE, ObjType.TRIGGER, ObjType.PROCEDURE, ObjType.FUNCTION, ObjType.SYNONYM);
}
