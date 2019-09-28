$.get('/api/leader')
    .done(function (data) {

        function scoreTable(data) {
            let tablaFormateada = addTableHTML(data);
            let tablaScore = document.getElementById("leaderBoard");
            tablaScore.innerHTML = tablaFormateada;
        }

        function addTableHTML(data) {
            var tabla = "<thead><tr><th>Full Name</th><th>Total</th><th>Won</th><th>Lost</th><th>Tied</th></tr></thead>";
            tabla += "<tbody>";

            data.forEach(function (pepito) {

                tabla += '<tr>';
                tabla += '<td>' + pepito.userName + '</td>';
                tabla += '<td>' + pepito.score.puntajeTotal + '</td>';
                tabla += '<td>' + pepito.score.juegosGanados + '</td>';
                tabla += '<td>' + pepito.score.juegosPerdidos + '</td>';
                tabla += '<td>' + pepito.score.juegosEmpatados + '</td>';
                tabla += '</tr>';

                tabla += '</tbody>';
            });
            return tabla;
        }
        scoreTable(data);
    });
