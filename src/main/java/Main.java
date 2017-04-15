import Exceptions.DuplicatedUsernameException;
import Exceptions.EmptyRequiredAttributeException;
import question.*;
import survey.Survey;
import user.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Main {

    private static final String WELCOME_MESSAGE = "Welcome to ENQUESTATOR 3000";
    private static final String CREATE_NEW_USER = "1";
    private static final String SELECT_EXISTING_USER = "2";
    private static final String CREATE_NEW_SURVEY = "1";
    private static final String LIST_EXISTING_SURVEYS = "2";
    private static final String SHOW_USER_INFO = "3";
    private static final String EXIT = "0";
    private static final String FREE_QUESTION = "1";
    private static final String MULTIVALUED_UNSORTED_QUALITATIVE_QUESTION = "2";
    private static final String NUMERIC_QUESTION = "3";
    private static final String SORTED_QUALITATIVE_QUESTION = "4";
    private static final String UNSORTED_QUALITATIVE_QUESTION = "5";
    private static final Boolean UNSORTED = Boolean.FALSE;
    private static final Boolean SORTED = Boolean.TRUE;
    private static BufferedReader br;

    public static void main(String[] args) throws IOException, EmptyRequiredAttributeException {

        System.out.println(WELCOME_MESSAGE);

        br = new BufferedReader(new InputStreamReader(System.in));
        User user = selectUser();

        showMainMenu();

        String option = br.readLine();
        while (!EXIT.equals(option)) {
            switch (option) {
                case CREATE_NEW_SURVEY:
                    createSurvey();
                    break;
                case LIST_EXISTING_SURVEYS:
                    Map<Integer, Survey> surveys = Survey.getSurveys();
                    for (Survey survey : surveys.values()) System.out.println(survey.getTitle());
                    break;
                case SHOW_USER_INFO:
                    System.out.println(user);
                    break;
                default:
                    System.out.println("Invalid option");
            }
            showMainMenu();
            option = br.readLine();
        }
    }

    private static User selectUser() throws IOException {
        User user = null;
        while (user == null) {
            showSelectUserMenu();
            switch (br.readLine()) {
                case CREATE_NEW_USER:
                    System.out.print("Enter the username for the new user: ");
                    String username = br.readLine();
                    System.out.print("Enter the name: ");
                    String name = br.readLine();
                    user = new User(username, name);
                    saveUser(user);
                    break;
                case SELECT_EXISTING_USER:
                    System.out.println("Select one of the following users: ");
                    Map<String, User> users = User.getUsers();
                    for (User existingUser : users.values()) System.out.println(existingUser.getUsername());
                    String selectedUser = br.readLine();
                    if (!users.containsKey(selectedUser)) System.out.println("Not a valid user");
                    else user = users.get(selectedUser);
                    break;
                default:
                    System.out.println("Invalid option");
            }

        }
        return user;
    }

    private static void showSelectUserMenu() {
        System.out.println();
        System.out.println("Choose one of the following options:");
        System.out.println(CREATE_NEW_USER + " - Create new user");
        System.out.println(SELECT_EXISTING_USER + " - Select existing user");
    }

    private static void createSurvey() throws IOException, EmptyRequiredAttributeException {
        System.out.println();
        System.out.println("New Survey:");
        Survey survey = new Survey();
        System.out.print("Enter survey's title: ");
        survey.setTitle(br.readLine());
        System.out.print("Enter survey's description: ");
        survey.setDescription(br.readLine());
        System.out.println("Add questions: ");
        showQuestionTypes();
        String option = br.readLine();
        while (!EXIT.equals(option)) {
            switch (option) {
                case FREE_QUESTION:
                    FreeQuestion q1 = new FreeQuestion();
                    setQuestionStatement(q1);
                    System.out.print("Enter answer's maximum size: ");
                    q1.setMaxSize(Integer.valueOf(br.readLine()));
                    survey.addQuestion(q1);
                    break;
                case MULTIVALUED_UNSORTED_QUALITATIVE_QUESTION:
                    MultivaluedUnsortedQualitativeQuestion q2 = new MultivaluedUnsortedQualitativeQuestion();
                    setQuestionStatement(q2);
                    addOptions(q2, UNSORTED);
                    System.out.print("Enter maximum number of selectable options: ");
                    q2.setnMaxAnswers(Integer.valueOf(br.readLine()));
                    survey.addQuestion(q2);
                    break;
                case NUMERIC_QUESTION:
                    NumericQuestion q3 = new NumericQuestion();
                    setQuestionStatement(q3);
                    System.out.print("Enter answer's minimum value: ");
                    q3.setMin(Integer.valueOf(br.readLine()));
                    System.out.print("Enter answer's maximum value: ");
                    q3.setMax(Integer.valueOf(br.readLine()));
                    survey.addQuestion(q3);
                    break;
                case SORTED_QUALITATIVE_QUESTION:
                    SortedQualitativeQuestion q4 = new SortedQualitativeQuestion();
                    setQuestionStatement(q4);
                    addOptions(q4, SORTED);
                    survey.addQuestion(q4);
                    break;
                case UNSORTED_QUALITATIVE_QUESTION:
                    UnsortedQualitativeQuestion q5 = new UnsortedQualitativeQuestion();
                    setQuestionStatement(q5);
                    addOptions(q5, UNSORTED);
                    survey.addQuestion(q5);
                    break;
                default:
                    System.out.println("Invalid option");
            }
            showQuestionTypes();
            option = br.readLine();
        }
        survey.save();
    }

    private static void setQuestionStatement(Question question) throws IOException {
        System.out.println("Enter question's statement: ");
        question.setStatement(br.readLine());
    }

    private static void showMainMenu() {
        System.out.println();
        System.out.println("Choose one of the following options:");
        System.out.println(CREATE_NEW_SURVEY + " - Create new survey");
        System.out.println(LIST_EXISTING_SURVEYS + " - List existing surveys");
        System.out.println(SHOW_USER_INFO + " - Show user info");
        System.out.println(EXIT + " - Exit");
    }

    private static void showQuestionTypes() {
        System.out.println();
        System.out.println("Choose one of the following types of questions:");
        System.out.println(FREE_QUESTION + " - Free Question");
        System.out.println(MULTIVALUED_UNSORTED_QUALITATIVE_QUESTION + " - Multivalued Unsorted Qualitative Question");
        System.out.println(NUMERIC_QUESTION + " - Numeric Question");
        System.out.println(SORTED_QUALITATIVE_QUESTION + " - Sorted Qualitative Question");
        System.out.println(UNSORTED_QUALITATIVE_QUESTION + " - Unsorted Qualitative Question");
        System.out.println(EXIT + " - Exit");
    }

    private static void saveUser(User user) throws IOException {
        try {
            user.save();
        } catch (DuplicatedUsernameException e) {
            System.out.println("Username is already taken");
            System.out.print("Enter a new one: ");
            user.setUsername(br.readLine());
            saveUser(user);
        } catch (EmptyRequiredAttributeException e) {
            e.printStackTrace();
        }
    }

    private static void addOptions(QualitativeQuestion question, Boolean sorted) throws IOException {
        System.out.print("Enter option's value: ");
        String value = br.readLine();
        Option option;
        if (sorted) {
            System.out.print("Enter option's weight: ");
            option = new Option(value, Integer.valueOf(br.readLine()));
        } else {
            option = new Option(value);
        }
        question.addOption(option);

        System.out.println("¿Would you like to add another option? (Y/N)");
        if ("Y".equals(br.readLine())) addOptions(question, sorted);
    }
}
