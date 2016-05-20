package controllers;

import models.User;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.logged;
import views.html.login;
import views.html.signup;

import java.sql.Timestamp;
import java.util.Date;

import static play.data.Form.form;

public class Application extends Controller {


    public static class Register {


        @Constraints.Required
        public String name;
        @Constraints.Required
        public String email;

        @Constraints.Required
        public String password;
        @Constraints.Required
        public String level;
        public Timestamp doneAt = new Timestamp(new Date().getTime());


        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            if (isBlank(name)) {
                return "Email is required";
            }
            if (isBlank(email)) {
                return "Email is required";
            }

            if (isBlank(password)) {
                return "Full name is required";
            }
            if (isBlank(level)) {
                return "Full name is required";
            }

            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }
    }


    /**
     * Defines a form wrapping the User class.
     */

    final static Form<Application.Register> signupForm = form(Register.class);

    /**
     * Display a blank form.
     */
    public static Result show() {

        return ok(signup.render(form(Application.Register.class)));
    }

    private static Result checkBeforeSave(Form<Application.Register> registerForm, String email) {
        // Check unique email
        if (User.findByEmail(email) != null) {
            flash("error", Messages.get("This email" + email + "Already exist"));
            return badRequest(signup.render(registerForm));
        }

        return null;
    }

    /**
     * Handle the form submission.
     */
    public static Result register() {

        Form<Application.Register> filledForm = signupForm.bindFromRequest();
        // Check if form is valid

        if (filledForm.hasErrors()) {
            return badRequest(views.html.signup.render(filledForm));
        }
        Application.Register register = filledForm.get();
        Result resultError = checkBeforeSave(filledForm, register.email);

        if (resultError != null) {
            return resultError;

        }

        try {
            User user = new User();
            user.email = register.email;
            user.password = register.password;
            user.name = register.name;
            user.level = register.level;
            user.save();

            return ok("saved");
        } catch (Exception e) {
            Logger.error("Signup.save error", e);
            flash("error", Messages.get("error.technical"));
        }
        return badRequest(signup.render(filledForm));
    }



    // -- Authentication
    
    public static class Login {
        @Constraints.Required
        public String email;

        @Constraints.Required
        public String password;


        public String validate() {

            if(User.authenticate(email, password) == null) {

                return "Invalid user or password";
            }
            else{
                flash("error", "Field can not be empty");
            }
            return null;
        }
    }

    /**
     * Login page.
     */
    public static Result login() {
        return ok(
            login.render(form(Login.class))
        );
    }
    public static Result profile() {
        Form<User> loginForm = Form.form(User.class);
        return ok(logged.render(User.findAll(),loginForm));
    }
    /**
     * Handle login form submission.
     */
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();

        if(loginForm.hasErrors()) {
            flash("error", "Field can not be empty");
            return badRequest(login.render(loginForm));
        } else {
            session("email", loginForm.get().email);
            return redirect(routes.Application.profile())            ;
        }
    }

    /**
     * Logout and clean the session.
     */
    public static Result logout() {
        session().clear();
        flash("success", "You've been logged out");
        return redirect(
            routes.Application.login()
        );
    }
  


}
