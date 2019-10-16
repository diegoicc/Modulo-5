$(function () {
  loadData();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};

// Grid type = salvo / ship
const createGrid = (size, gridId, gridType) => {
  let gridContainer = document.querySelector('.' + gridId);
  for (let i = 0; i < size; i++) {
    let row = document.createElement('div');
    let rowId = String.fromCharCode(i + 64).toLowerCase();
    gridContainer.appendChild(row);
    for (let j = 0; j < size; j++) {
      // Creates a div (cell) for each row.
      let cell = document.createElement('div');
      cell.classList.add('gridCell');
      if (i > 0 && j > 0) {
        //example: id="salvog5" / id="shipc3"
        cell.id = gridType + rowId + j;
      }
      if (j === 0 && i > 0) {
        // Adds header's column name.
        cell.classList.add('gridHeader');
        cell.innerText = String.fromCharCode(i + 64);
      }
      if (i === 0 && j > 0) {
        // Adds header's row name.
        cell.classList.add('gridHeader');
        cell.innerText = j;
      }
      row.appendChild(cell)
    }
  }
}

function loadData() {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
      console.log(data);
      let playerInfo;
      if (data.gamePlayers[0].id == getParameterByName('gp'))
        playerInfo = [data.gamePlayers[0].player.email, data.gamePlayers[1].player.email];
      else
        playerInfo = [data.gamePlayers[1].player.email, data.gamePlayers[0].player.email];
      $('#playerInfo').text(playerInfo[0] + '(you) vs ' + playerInfo[1]);

      createGridShip(data.ships);
      createGridSalvo(data.salvoEs);
    })

    .fail(function (jqXHR, textStatus) {
      alert("Failed: " + textStatus);
    });

  function createGridShip(ships) {
    ships.forEach(function (shipPiece) {
      shipPiece.location.forEach(function (location) {
        $('#ship' + location.toLowerCase()).addClass('ship-piece');

         function isHit(shipLocations, salvos, playerId) {
            var turn = 0;

            salvos.forEach(function (salvo) {
              if (salvo.player != playerId)
                salvo.salvoLocations.forEach(function (location) {
                  if (shipLocations === location)
                    turn = salvo.turn;
                });
            });
            return turn;
          }
      })
    });
  }

  function createGridSalvo(salvos) {
    salvos.forEach(function (element) {
      console.log(element);
      element.salvoLocation.forEach(function (location) {
        console.log(location);
        $('#salvo' + location.toLowerCase()).addClass('salvoPiece');

        if (isHit(location, data.salvoEs, playerInfoId[0]) != 0) {
          $('#ship' + location.toLowerCase()).addClass('shipPieceHited');
          $('#ship' + location.toLowerCase()).text(isHit(location, data.salvoEs, playerInfoId[0].id));
        } else
          $('#ship' + location.toLowerCase()).addClass('shipPiece');
      });
    });
  }

 createGrid(11, "gridShips", "ship");
  createGrid(11, "gridSalvoes", "salvo");

};