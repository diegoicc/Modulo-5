var miInit = {
   method: 'GET',
};
fetch('/api/games', miInit)
   .then(function (response) {
      return response.json();
   })

   .then(function (data) {
      var addHTML = document.getElementById("gamesInfo");
      /*  data.map(element => {
         var gamesList = document.createElement("li");
         gamesList.appendChild(document.createTextNode("Juego " + element.id + "    " + "Fecha: " + new Date(element.created).toLocaleString() + " " + "Jugador 1: " + element.gamePlayers[0].player.email + " VS " + "Jugador 2: " + element.gamePlayers[1].player.email));
         document.body.insertBefore(gamesList, addHTML);
      });   */

      addHTML.innerHTML = data.games.map(element => {
         return "<tr><td> " + element.id + " </td> <td>" +
            new Date(element.created).toLocaleString() +
            " </td>" + element.gamePlayers
               .map(function (p) {
                  return "<td>" + p.player.email + "</td>"
               }).join('')
      }).join(' ') + "</tr>";
   });