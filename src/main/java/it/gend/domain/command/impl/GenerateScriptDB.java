package it.gend.domain.command.impl;

import it.gend.configuration.Constant;
import it.gend.configuration.Event;
import it.gend.configuration.ObjType;
import it.gend.connection.JDBCConnector;
import it.gend.domain.RecordDDL;
import it.gend.domain.ScriptVersion;
import it.gend.repository.GenerateScriptAccess;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * @author Daniele Asteggiante
 */
public class GenerateScriptDB extends AbstractCommand {
    private Connection connection;
    GenerateScriptAccess generateScriptAccess;

    @Override
    public void execute() {
        try {
            Date startDate = getStartDate();
            System.out.println("Start date: " + startDate);
            List<RecordDDL> recordDDLs = generateScriptAccess.getRecordDDLs(connection, startDate);
            String rawScript = generateRawScript(recordDDLs);
            String scriptCleaned = generateCleanedScript(rawScript);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateCleanedScript(String rawScript) {
        System.out.println("Generate Cleaned Script...");
        String[] lines = rawScript.split(Constant.recordDLLSeparator);
        String scriptWithoutContraddiction = geenerateScriptWithoutContraddiction(lines);
        return null;
    }

    private String geenerateScriptWithoutContraddiction(String[] lines) {
        System.out.println("Generate Script Without Contraddiction...");
        StringBuilder script = new StringBuilder();
        for (String line : lines) {
            String lineWithoutComments = removeComments(line);
            ObjType type = findSqlType(lineWithoutComments);
            String name = findName(type, lineWithoutComments);
        }
        return script.toString();
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

    private String convertToSQLAction(String objTypeStr, String eventStr) {
        System.out.println("Convert To SQL Action...");
        ObjType objType1 = ObjType.valueOf(objTypeStr);
        Event event1 = Event.valueOf(eventStr);
        if (Constant.createOrReplaceble.contains(objType1) && event1.equals(Event.CREATE))
            return "CREATE OR REPLACE";
        return eventStr + " " + objTypeStr;
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
