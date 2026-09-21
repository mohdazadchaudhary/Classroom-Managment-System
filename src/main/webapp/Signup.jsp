<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign Up | IIIT-B Classroom Manager</title>
    <link href="//netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css" rel="stylesheet">
    <script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.0/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="/css/index.css">
    <style>
        body {
            background: url("/images/reduced_opacity_bg.jpeg") repeat-x;
            font-family: Ubuntu, Arial, sans-serif;
        }
        .signup-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 15px;
        }
        .signup-card {
            background: rgba(255, 255, 255, 0.96);
            border-radius: 10px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
            padding: 40px 36px 32px;
            width: 100%;
            max-width: 480px;
        }
        .signup-card h3 {
            color: #563D7C;
            font-weight: 700;
            margin-bottom: 6px;
        }
        .signup-card .subtitle {
            color: #888;
            font-size: 13px;
            margin-bottom: 24px;
        }
        .form-control {
            border-radius: 6px;
        }
        .btn-signup {
            background-color: #563D7C;
            border-color: #563D7C;
            color: #fff;
            border-radius: 6px;
            font-size: 15px;
            padding: 10px;
            margin-top: 8px;
            transition: background-color 0.2s;
        }
        .btn-signup:hover {
            background-color: #432f63;
            border-color: #432f63;
            color: #fff;
        }
        .login-link {
            text-align: center;
            margin-top: 18px;
            font-size: 13px;
        }
        .login-link a {
            color: #563D7C;
            font-weight: 600;
        }
        .alert-pending {
            background: #fff3cd;
            border: 1px solid #ffc107;
            color: #856404;
            border-radius: 6px;
            padding: 10px 14px;
            margin-bottom: 18px;
            font-size: 13px;
        }
        .alert-error {
            background: #f8d7da;
            border: 1px solid #f5c2c7;
            color: #842029;
            border-radius: 6px;
            padding: 10px 14px;
            margin-bottom: 18px;
            font-size: 13px;
        }
        #pass-match {
            font-size: 12px;
            margin-top: 4px;
        }
        label {
            font-weight: 600;
            margin-bottom: 4px;
            display: block;
        }
        .form-group { margin-bottom: 16px; }
    </style>
</head>
<body>

<div class="signup-wrapper">
    <div class="signup-card">

        <h3>Create an Account</h3>
        <p class="subtitle">IIIT-B Classroom Manager &mdash; your request will be reviewed by an admin before activation.</p>

        <%-- Flash: error from duplicate username --%>
        <%
            String signupError = (String) session.getAttribute("signup_error");
            if (signupError != null) {
                session.removeAttribute("signup_error");
        %>
        <div class="alert-error">&#9888; <%= signupError %></div>
        <%  } %>

        <form action="/signup" id="signup-form" method="post">

            <div class="form-group">
                <label for="usertype">Role</label>
                <select class="form-control" id="usertype" name="usertype" required>
                    <option value="">Select your role</option>
                    <option value="professor">Professor</option>
                    <option value="ta">Teaching Assistant (TA)</option>
                    <option value="committee">Committee</option>
                    <option value="sac">SAC</option>
                </select>
            </div>

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" class="form-control"
                       placeholder="Your full name" required>
            </div>

            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" class="form-control"
                       placeholder="Choose a username" required>
            </div>

            <div class="form-group">
                <label for="email">Email Address</label>
                <input type="email" id="email" name="email" class="form-control"
                       placeholder="Your institutional email" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="At least 4 characters" minlength="4" required>
            </div>

            <div class="form-group">
                <label for="confirm-password">Confirm Password</label>
                <input type="password" id="confirm-password" name="confirm-password" class="form-control"
                       placeholder="Repeat your password" minlength="4" required>
                <p id="pass-match"></p>
            </div>

            <div class="alert-pending">
                &#8505; After signing up, your account will be <strong>pending admin approval</strong>.
                You will be able to log in once an admin activates your account.
            </div>

            <button class="btn btn-signup btn-block" type="submit" id="submit-btn">Request Account</button>
        </form>

        <div class="login-link">
            Already have an account? <a href="/index.html">Log In</a>
        </div>
    </div>
</div>

<script>
    // Real-time password match validation
    $('#password, #confirm-password').on('keyup', function () {
        var p1 = $('#password').val();
        var p2 = $('#confirm-password').val();
        if (p2.length === 0) {
            $('#pass-match').text('');
        } else if (p1 === p2) {
            $('#pass-match').text('Passwords match \u2714').css('color', 'green');
        } else {
            $('#pass-match').text('Passwords do not match').css('color', '#c00');
        }
    });

    // Block submit if passwords don't match
    $('#signup-form').on('submit', function (e) {
        if ($('#password').val() !== $('#confirm-password').val()) {
            e.preventDefault();
            $('#pass-match').text('Passwords do not match — please fix before submitting.').css('color', '#c00');
        }
    });
</script>

</body>
</html>
