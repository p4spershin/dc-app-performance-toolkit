package bamboogenerator;

import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.TestParserTask;
import com.atlassian.bamboo.specs.model.task.TestParserTaskProperties;

public class Tasks {

    public static ScriptTask writeTestXMLResults(Boolean failed, int testCount) {
        if (!failed) {
            return new ScriptTask()
                    .description("Write XML test results")
                    .interpreterBinSh()
                    .inlineBody(String.format("rm -f *.xml\n" +
                            "cat << EOF >> success.xml\n" +
                            "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<testsuites>\n" +
                            "<testsuite name=\"pytest\" errors=\"0\" failures=\"0\" skipped=\"0\" tests=\"%d\" time=\"1.6\" timestamp=\"2021-08-04T13:12:34.674907\" hostname=\"TEST_HOST\">\n" +
                            "EOF\n" +
                            "i=1\n" +
                            "while [ \"$i\" -le %d ]; do echo \"<testcase classname=\\\"DcaptBambooTests\\\" name=\\\"test_case_$i\\\" time=\\\"0.16\\\"/>\" >> success.xml;i=$(( i + 1 )); done\n" +
                            "cat << EOF >> success.xml\n" +
                            "</testsuite>\n" +
                            "</testsuites>\n" +
                            "EOF", testCount, testCount
                    ));
        } else {
            return new ScriptTask()
                    .description("Write XML test results")
                    .interpreterBinSh()
                    .inlineBody(String.format("rm -f *.xml\n" +
                            "cat << EOF >> failed.xml\n" +
                            "<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                            "<testsuites>\n" +
                            "<testsuite name=\"pytest\" errors=\"0\" failures=\"1\" skipped=\"0\" tests=\"%d\" time=\"1.6\" timestamp=\"2021-08-04T13:12:34.674907\" hostname=\"TEST_HOST\">\n" +
                            "EOF\n" +
                            "i=1\n" +
                            "while [ \"$i\" -le %d ]; do\n" +
                            "if [ $i -eq %d ]; then\n" +
                            "echo \"<testcase classname=\\\"DcaptBambooTests\\\" name=\\\"test_case_$i\\\" time=\\\"0.16\\\">\" >> failed.xml;i=$(( i + 1 ));\n" +
                            "else\n" +
                            "echo \"<testcase classname=\\\"DcaptBambooTests\\\" name=\\\"test_case_$i\\\" time=\\\"0.16\\\"/>\" >> failed.xml;i=$(( i + 1 ));\n" +
                            "fi\n" +
                            "done\n" +
                            "cat << EOF >> failed.xml\n" +
                            "<failure message=\"AssertionError: DCAPT TEST ERROR\">Test assertion error</failure></testcase>\n" +
                            "</testsuite>\n" +
                            "</testsuites>\n" +
                            "EOF", testCount, testCount, testCount
                    ));
        }}

    public static TestParserTask getTestParserTask(Boolean failed) {
        if (failed) {
            return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                    .description("Unit test results parser task")
                    .resultDirectories("failed.xml");
        } else {
            return new TestParserTask(TestParserTaskProperties.TestType.JUNIT)
                    .description("Unit test results parser task")
                    .resultDirectories("success.xml");
        }
    }
}
