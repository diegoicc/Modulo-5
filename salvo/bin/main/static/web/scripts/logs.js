fetch('/api/games')
    .then(function (response) {
        return response.json();
    })
    .then(function (data) {
        if (data.player == "Guest") {
            $("#login-form").show();
            $("#logout-form").hide();
            // $create juegos
        } else {
            $("#userLog").append("Hello: " + data.player.email);
            $("#login-form").hide();
            $("#logout-form").show();
        }
        console.log(data)
    })


$("#login").click(function (evt) {
    login(evt);
})

function login(e) {
    e.preventDefault();
    $.post("/api/login", {
            name: $("#username").val(),
            password: $("#password").val()

        }).done(function () {
            $("#logout-form").show(),
                $("#password").val(""),
                console.log("Hola")
            alert("Hola: " + $('#username').val());
            location.reload();
            $("#login-form").hide();
            $("#userLog").append("Hola: " + $('#username').val());
            $("#logout-form").show();
        })
        .fail(function () {
            console.log("Failed to LogIn");
            alert("Error")
        })
}

$("#logout").click(function (evt) {
    logout(evt);
})

function logout(e) {
    e.preventDefault();
    $.post("/api/logout")
        .done(function () {
            console.log("Bye");
            alert("Bye : " + $('#username').val());
            location.reload();
            $("#logout-form").hide();
            $("#login-form").show()
        })
        .fail(function () {
            alert("Failed to LogOut")
        });
}

$("#singUp").click(function (evt) {
    signUp(evt);
})

function signUp(e) {
    e.preventDefault();
    $.post("/api/players", {
            email: $("#username").val(),
            password: $("#password").val()
        })
        .done(function () {
            console.log("Bienvenido a tu juego");
            login(e);
            leaderBoard();
            $("#login-form").hide(),
                $("#logout-form").show(),
                $("#userLog").append("Hola: " + $('#username').val());
            $("#password").val("")
        })
        .fail(function () {
            alert("Failed to LogIn");
            alert("User not registered")
        });
}