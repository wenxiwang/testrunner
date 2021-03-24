package edu.illinois.cs.testrunner.execution;

import edu.illinois.cs.diaper.StateCapture;
import edu.illinois.cs.diaper.agent.MainAgent;
import org.apache.commons.io.FileUtils;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TestListener extends RunListener {
    private final Map<String, Long> times;
    private final Map<String, Double> testRuntimes;
    private final Set<String> ignoredTests;

    public TestListener() {
        testRuntimes = new HashMap<>();
        times = new HashMap<>();
        ignoredTests = new HashSet<>();
    }

    public Set<String> ignored() {
        return ignoredTests;
    }

    public Map<String, Double> runtimes() {
        return testRuntimes;
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        ignoredTests.add(JUnitTestRunner.fullName(description));
    }

    private String readFile(String path) throws IOException {
        File file = new File(path);
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        //times.put(JUnitTestRunner.fullName(description), System.nanoTime());
        String fullTestName = JUnitTestRunner.fullName(description);
        times.put(fullTestName, System.nanoTime());

        String phase = readFile(MainAgent.tmpfile);
        if(MainAgent.targetTestName.equals(fullTestName)) {
            if(phase.equals("3") || phase.equals("4")) {
                StateCapture sc = new StateCapture(fullTestName);
                System.out.println("MainAgent.targetTestName: " + MainAgent.targetTestName +
                        " fullTestName: " + fullTestName);
                System.out.println("phase: " + phase);
                System.out.println("test listener!!!!!!!!! Capturing the states!!!!!!!!!!!!!");
                sc.capture();
                //System.out.println("sc.dirty: " + sc.dirty);
            }
            else if(phase.equals("5")) {
                StateCapture sc = new StateCapture(fullTestName);
                System.out.println("MainAgent.targetTestName: " + MainAgent.targetTestName +
                        " fullTestName: " + fullTestName);
                System.out.println("phase: " + phase);
                System.out.println("test listener!!!!!!!!! diffing the fields in passorder!!!!!!!!!!!!!");
                sc.diffing();
            }
            /*else if(phase.startsWith("diffFieldBefore ")) {
                System.out.println("test listener!!!!!!!!! reflection on the states before!!!!!!!!!!!!!");
                StateCapture sc = new StateCapture(fullTestName);
                String diffField = phase.replaceFirst("diffFieldBefore ", "");
                sc.fixing(diffField);
            }*/
            System.out.println("testStarted end!!");
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        failure.getException().printStackTrace();
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        failure.getException().printStackTrace();
    }

    @Override
    public void testFinished(Description description) throws Exception {
        final String fullTestName = JUnitTestRunner.fullName(description);

        if (times.containsKey(fullTestName)) {
            final long startTime = times.get(fullTestName);
            testRuntimes.put(fullTestName, (System.nanoTime() - startTime) / 1E9);
        } else {
            System.out.println("Test finished but did not start: " + fullTestName);
        }

        String phase = readFile(MainAgent.tmpfile);

            if(phase.startsWith("4")) {
                String polluter = phase.split(" ")[1];
                if(polluter.equals(fullTestName)) {
                    StateCapture sc = new StateCapture(fullTestName);
                    System.out.println("MainAgent.targetTestName: " + MainAgent.targetTestName +
                            " fullTestName: " + fullTestName);
                    System.out.println("phase: " + phase);
                    System.out.println("test listener at after!!!!!!!!! Capturing the states!!!!!!!!!!!!!");
                    sc.capture();
                }
            }
            else if(phase.startsWith("diffFieldAfter ")) {
                String polluter = phase.split(" ")[1];
                if(polluter.equals(fullTestName)) {
                    // reflect one field each time
                    if(phase.split(" ").length == 3) {
                        System.out.println("test listener at after!!!!!!!!! reflection on the states after!!!!!!!!!!!!!");
                        StateCapture sc = new StateCapture(fullTestName);
                        String diffField = phase.split(" ")[2];
                        sc.fixing(diffField);
                    }
                    //reflect two fields
                    /*else if(phase.split(" ").length == 4){
                        StateCapture sc = new StateCapture(fullTestName);
                        List<String> fields = new ArrayList<>();
                        String diffField1 = phase.split(" ")[2];
                        String diffField2 = phase.split(" ")[3];
                        fields.add(diffField1);
                        fields.add(diffField2);
                        sc.fixingFList(fields);
                    }*/
                }
            }

        if(MainAgent.targetTestName.equals(fullTestName)) {
            if(phase.equals("2")) {
                System.out.println("MainAgent.targetTestName: " + MainAgent.targetTestName +
                        " fullTestName: " + fullTestName);
                System.out.println("phase: " + phase);

                StateCapture sc = new StateCapture(fullTestName);//CaptureFactory.StateCapture(fullTestName);
                System.out.println("test listener at after !!!!!!!!! Capturing the states!!!!!!!!!!!!!");
                sc.capture();
                //System.out.println("sc.dirty: " + sc.dirty);
            }
            else if(phase.equals("5doublevic")) {
                StateCapture sc = new StateCapture(fullTestName);
                System.out.println("MainAgent.targetTestName: " + MainAgent.targetTestName +
                        " fullTestName: " + fullTestName);
                System.out.println("phase: " + phase);
                System.out.println("test listener!!!!!!!!! diffing the fields in doublevictim order!!!!!!!!!!!!!");
                sc.diffing();
            }
        }
    }
}
