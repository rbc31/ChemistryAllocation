package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import Exceptions.ConfigNotValidException;
import dataManager.Config;
import dataManager.Students;
import dataManager.Supervisors;
import main.Table;
import matcher.Matcher;
import output.Output;
import utils.MatchingUtils;

/**
 * Tests for matching algorithm and logic
 * @author Rob
 *
 */
public class MatchingTests {

	/**
	 * Simple test to test if preference allocation works with 1 student and 1 supervisor
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_simple() throws Exception {
		Config.configFile = "test_files/config_files/simplevalid1.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = new Supervisors();
				
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(1, matching.size());
		assertTrue(matching.containsKey("username_1"));
		assertEquals("supervisor_1", matching.get("username_1"));
		
		Config.saveMatching(matching);
		
		HashMap<String,String> loadedMatch = Config.loadMatching();
		
		assertEquals(1, loadedMatch.size());
		assertTrue(loadedMatch.containsKey("username_1"));
		assertEquals("supervisor_1", loadedMatch.get("username_1"));
		
		String expectedStr = "Number of students allocated: 1 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"           1: 1  (100.0%)\n" + 
				"\n" + 
				"Number of people allocated by choice: 1\n" + 
				"Number of people allocated on keyword/topic area: 0\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n";
		
		String actualStr = MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.STUDENT_TOPIC_AREA_COLUMNS));
	
		assertEquals(expectedStr,actualStr);
	
		
		Config.configFile = "test_files/corrupt.json";
		Config.clearConfig();
		try {
			Matcher.allocate(students, supervisors, 100, warnings);
			fail("Expected exception");
		}catch (ConfigNotValidException e) {
			//pass
		}
		
		Config.configFile = "test_files/config_files/simplevalid1.json";
		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/simple_student_test_1.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/simple_supervisor_test_1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_1() throws Exception {
		Config.configFile = "test_files/config_files/complexvalid1.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
				
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),matching);
		
		Config.saveMatching(matching);
		
		HashMap<String,String> loadedMatch = Config.loadMatching();
		
		assertEquals(20, loadedMatch.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),loadedMatch);
	
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_1.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * Matching test on topic areas only 
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_2() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid2.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),matching);
		
		Config.saveMatching(matching);
		
		HashMap<String,String> loadedMatch = Config.loadMatching();
		
		assertEquals(20, loadedMatch.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),loadedMatch);
		
		String expectedEvaluation = "Number of students allocated: 20 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"\n" + 
				"Number of people allocated by choice: 0\n" + 
				"Number of people allocated on keyword/topic area: 20\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 5  (25.0%)\n" + 
				"              inorganic: 5  (25.0%)\n" + 
				"              organic: 5  (25.0%)\n" + 
				"              computational: 5  (25.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));

		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_2.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * Matching test on keywords only when keyword lower bound is greater than number of keywords. 
	 * -> no matches
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_3() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid3.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(0, matching.size());
		
		String expectedEvaluation = "Number of students allocated: 0 (0.0%)\n" + 
				"Number of students not allocated: 20 (100.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"\n" + 
				"Number of people allocated by choice: 0\n" + 
				"Number of people allocated on keyword/topic area: 0\n" + 
				"\n" + 
				"Number of supervisors with no students: 4\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 0  (0.0%)\n" + 
				"              inorganic: 0  (0.0%)\n" + 
				"              organic: 0  (0.0%)\n" + 
				"              computational: 0  (0.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));

		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_3.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		//as no matching took place the output supervisor should equal the input supervisor
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/supervisor_files/complexvalid1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		
	}
	
	/**
	 * Matching test on keywords only.
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_4() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid4.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_2_expected.json"),matching);
		
		String expectedEvaluation = "Number of students allocated: 20 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"\n" + 
				"Number of people allocated by choice: 0\n" + 
				"Number of people allocated on keyword/topic area: 20\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 5  (25.0%)\n" + 
				"              inorganic: 5  (25.0%)\n" + 
				"              organic: 5  (25.0%)\n" + 
				"              computational: 5  (25.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));
		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_4.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_4.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * Matching test on keywords and preferences.
	 * However preference weights are significantly lower than keywords.
	 * So in effect only choices matter
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_5() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid5.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),matching);
		
		String expectedEvaluation = "Number of students allocated: 20 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"           1: 20  (100.0%)\n" + 
				"           2: 0  (0.0%)\n" + 
				"           3: 0  (0.0%)\n" + 
				"           4: 0  (0.0%)\n" + 
				"\n" + 
				"Number of people allocated by choice: 20\n" + 
				"Number of people allocated on keyword/topic area: 0\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 5  (25.0%)\n" + 
				"              inorganic: 5  (25.0%)\n" + 
				"              organic: 5  (25.0%)\n" + 
				"              computational: 5  (25.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));
		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_5.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * Matching test on topic areas and preferences.
	 * However preference weights are significantly lower than topic areas.
	 * So in effect only choices matter
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_6() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid6.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_1_expected.json"),matching);
		
		String expectedEvaluation = "Number of students allocated: 20 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" + 
				"           1: 20  (100.0%)\n" + 
				"           2: 0  (0.0%)\n" + 
				"           3: 0  (0.0%)\n" + 
				"           4: 0  (0.0%)\n" + 
				"\n" + 
				"Number of people allocated by choice: 20\n" + 
				"Number of people allocated on keyword/topic area: 0\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 5  (25.0%)\n" + 
				"              inorganic: 5  (25.0%)\n" + 
				"              organic: 5  (25.0%)\n" + 
				"              computational: 5  (25.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));
		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_6.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_1.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}
	
	/**
	 * Matching test on topic areas and key words. No preference picks.
	 * However keywords weights are significantly lower than topic areas.
	 * So in effect only keywords matter
	 * @throws Exception if error
	 */
	@Test
	public void test_matching_complex_7() throws Exception {

		Config.configFile = "test_files/config_files/complexvalid7.json";
		
		Config.reload();
		Students students = Students.forceLoad();
		Supervisors supervisors = Supervisors.forceLoad();
		
		ArrayList<String> warnings = new ArrayList<String>();
		HashMap<String,String> matching = Matcher.allocate(students, supervisors, 100, warnings);
		
		assertEquals(0,warnings.size());
		assertEquals(Config.loadMatching("test_files/matching_files/complex_matching_2_expected.json"),matching);
		
		String expectedEvaluation = "Number of students allocated: 20 (100.0%)\n" + 
				"Number of students not allocated: 0 (0.0%)\n" + 
				"\n" + 
				"Break down of student matches via choices:\n" +  
				"\n" + 
				"Number of people allocated by choice: 0\n" + 
				"Number of people allocated on keyword/topic area: 20\n" + 
				"\n" + 
				"Number of supervisors with no students: 0\n" + 
				"\n" + 
				"Number of students in each topic area:\n" + 
				"              physical: 5  (25.0%)\n" + 
				"              inorganic: 5  (25.0%)\n" + 
				"              organic: 5  (25.0%)\n" + 
				"              computational: 5  (25.0%)\n";
		
		assertEquals(expectedEvaluation,MatchingUtils.evaulateMatching(matching, students, supervisors, Config.getConfig().getStrListValue(Config.MATCHING_TOPIC_AREAS)));
		
		Output.saveStudentOutput(students, supervisors, matching, "output.csv");
		Table expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_student_test_7.csv"));
		Table actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
		expectedOutput.equals(actualOutput);
		
		Output.saveSupervisorOutput(supervisors, students, matching, "output.csv");
		expectedOutput = Table.parseTableFromCSVFile(new File("test_files/matching_files/complex_supervisor_test_4.csv"));
		actualOutput   = Table.parseTableFromCSVFile(new File("output.csv"));
		assertEquals(expectedOutput,actualOutput);
	}

}
