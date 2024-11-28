package it.gend.domain.command.impl;

import it.gend.configuration.Constant;
import it.gend.configuration.Event;
import it.gend.configuration.ObjType;
import it.gend.connection.JDBCConnector;
import it.gend.domain.RecordDDL;
import it.gend.domain.ScriptElement;
import it.gend.domain.ScriptVersion;
import it.gend.repository.GenerateScriptAccess;

import java.sql.Connection;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Daniele Asteggiante
 */
public class GenerateScriptDB extends AbstractCommand {
    private Connection connection;
    GenerateScriptAccess generateScriptAccess;
    private final String name = "GenerateScriptDB";


    @Override
    public void execute(String... args) {
        try {
            Date startDate = getStartDate();
            System.out.println("Start date: " + startDate);
            List<RecordDDL> recordDDLs = generateScriptAccess.getRecordDDLs(connection, startDate);
            String rawScript = generateRawScript(recordDDLs);
            String scriptCleaned = generateCleanedScript(rawScript);
            generateScriptAccess.saveScript(connection, "-","-", scriptCleaned);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCleanedScript(String rawScript) {
        System.out.println("Generate Cleaned Script...");
        String[] lines = rawScript.split(Constant.recordDLLSeparator);
        String scriptWithoutContradiction = generateScriptWithoutContradiction(lines);
        return removeSeparators(scriptWithoutContradiction);
    }

    private String removeSeparators(String script) {
        System.out.println("Remove Separators...");
        String scriptWithoutRecordDLLSeparators = script.replace(Constant.recordDLLSeparator, "");
        return scriptWithoutRecordDLLSeparators.replaceAll(Constant.beginClassification + ".*?" + Constant.endClassification, "");
    }

    private String generateScriptWithoutContradiction(String[] lines) {
        System.out.println("Generate Script Without Contradiction...");
        Map<String, ScriptElement> objectEventsMap = getStringScriptElementMap(lines);
        Map<String, ScriptElement> objectEventsMapSorted = sortMap(objectEventsMap);
        return objectEventsMapToString(objectEventsMapSorted);
    }

    private Map<String, ScriptElement> sortMap(Map<String, ScriptElement> objectEventsMap) {
        System.out.println("Sort Map...");
        for (String key : objectEventsMap.keySet()) {
            ScriptElement scriptElement = objectEventsMap.get(key);
            if (!Event.CREATE.equals(scriptElement.getEvent())) continue;
            for (String key2 : objectEventsMap.keySet()) {
                if (key.equals(key2)) continue;
                ScriptElement scriptElement2 = objectEventsMap.get(key2);
                if (isCalledBeforeCreation(key, scriptElement, scriptElement2)) {
                    objectEventsMap.remove(key2);
                    objectEventsMap.put(key, scriptElement);
                    objectEventsMap.put(key2, scriptElement);
                }
            }
        }
        return objectEventsMap;
    }

    private boolean isCalledBeforeCreation(String key, ScriptElement scriptElement, ScriptElement scriptElement2) {
        System.out.println("Is Called Before Creation...");
        return scriptElement2.getLine().contains(key) && scriptElement2.getLineNumber() < scriptElement.getLineNumber();
    }

    private Map<String, ScriptElement> getStringScriptElementMap(String[] lines) {
        Map<String, ScriptElement> objectEventsMap = new LinkedHashMap<>();
        int lineNumber = 0;
        for (String line : lines) {
            String lineWithoutComments = removeComments(line);
            ObjType type = findSqlType(lineWithoutComments);
            Event event = findSqlEvent(lineWithoutComments);
            String name = findName(type, lineWithoutComments);
            addElementToMap(objectEventsMap, type, event, name, lineWithoutComments, lineNumber++);
        }
        return objectEventsMap;
    }

    private String objectEventsMapToString(Map<String, ScriptElement> objectEventsMap) {
        System.out.println("Object Events Map To String...");
        StringBuilder script = new StringBuilder();
        for (ScriptElement scriptElement : objectEventsMap.values()) {
            script.append(scriptElement.getLine());
        }
        return script.toString();
    }

    private void addElementToMap(Map<String, ScriptElement> objectEventsMap, ObjType type, Event event, String name, String lineWithoutComments, int lineNumber) {
        System.out.println("Add Element To Map...");
        if (objectEventsMap.containsKey(name)) {
            checkContradiction(objectEventsMap, type, event, name, lineWithoutComments, lineNumber);
            return;
        }
        objectEventsMap.put(name, new ScriptElement(name, type, event, lineWithoutComments, lineNumber));
    }

    private void checkContradiction(Map<String, ScriptElement> objectEventsMap, ObjType type, Event event, String name, String lineWithoutComments, int lineNumber) {
        System.out.println("Check Contradiction...");
        if (Event.DROP.equals(event)) {
            objectEventsMap.remove(name);
            return;
        }
        if (Event.CREATE.equals(event) && ObjType.VIEW.equals(type))
            objectEventsMap.put(name, new ScriptElement(name, type, event, lineWithoutComments, lineNumber));
    }

    private Event findSqlEvent(String line) {
        System.out.println("Find SQL Evenr...");
        String classification = line.split(Constant.beginClassification)[1];
        String[] classificationParts = classification.split(Constant.elementClassificationSeparator);
        return Event.valueOf(classificationParts[1]);
    }

    private String findName(ObjType type, String lineWithoutComments) {
        System.out.println("Find Name...");
        String[] parts = lineWithoutComments.split(type.name());
        return parts[1].split(" ")[0];
    }

    private String removeComments(String line) {
        System.out.println("Remove Comments...");
        StringBuilder lineWithoutComments = new StringBuilder();
        String[] parts = line.split("\n");
        for (String part : parts) {
            if (part.startsWith("--")) continue;
            lineWithoutComments.append(part);
        }
        return lineWithoutComments.toString();
    }

    private ObjType findSqlType(String line) {
        System.out.println("Find SQL Action...");
        String classification = line.split(Constant.beginClassification)[1];
        String[] classificationParts = classification.split(Constant.elementClassificationSeparator);
        return ObjType.valueOf(classificationParts[0]);

    }

    private String generateRawScript(List<RecordDDL> recordDDLs) {
        System.out.println("Generate Raw Script...");
        StringBuilder script = new StringBuilder();
        for (RecordDDL recordDDL : recordDDLs) {
            ObjType objType = ObjType.valueOf(recordDDL.getOraDictObjType());
            Event event = Event.valueOf(recordDDL.getOraSysEvent());
            boolean isView = objType.equals(ObjType.VIEW);
            boolean operationIsCreate = event.equals(Event.CREATE);
            if (isView && !operationIsCreate) continue;
            String classification = generateClassification(objType, event);
            script.append(Constant.beginClassification)
                    .append(classification)
                    .append(Constant.endClassification)
                    .append(Constant.recordDLLSeparator);
            script.append(recordDDL.getDdlText());
        }
        return script.toString();
    }

    private String generateClassification(ObjType objType, Event event) {
        System.out.println("Generate Classification...");
        return objType.name() + Constant.elementClassificationSeparator + event.name();
    }


    private Date getStartDate() {
        System.out.println("Get Start Date...");
        connection = JDBCConnector.getConnection();
        generateScriptAccess = new GenerateScriptAccess();
        ScriptVersion scriptVersion = generateScriptAccess.getLastScriptVersion(connection);
        if (scriptVersion != null)
            return new Date(scriptVersion.getScriptDate().getTime());
        return new Date(System.currentTimeMillis());
    }

}
