package controllers;

import models.User;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.logged;
import views.html.login;
import views.html.signup;

import static play.data.Form.form;

public class Application extends Controller {

    /**
     * Defines a form wrapping the User class.
     */

    final static Form<User> signupForm = form(User.class);
    /**
     * Display a blank form.
     */
    public static Result show() {

        return ok(signup.render(signupForm));
    }



    /**
     * Handle the form submission.
     */
    public static Result register() {

        Form<User> filledForm = signupForm.bindFromRequest();
        // Check if form is valid

        if(filledForm.hasErrors()) {
            flash("error", "check form error.");
            return badRequest(views.html.signup.render(filledForm));
        } else {
            User created = filledForm.get();
            created.save();
            return redirect(routes.Application.login());
        }
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
