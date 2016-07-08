function getDefaultModel() {
    return {
        "userToken": "",
        "dealer": false,
        "rightGuess": false,
        "hand": [{
            "token": "",
            "source": "",
            "metadata": {
                "author": "",
                "externalUrl": "",
                "description": ""
            }
        }],
        "gameView": {
            "id": "",
            "name": "",
            "status": "",
            "tags": "",
            "remainingTime": 0,
            "players": [{
                "token": "",
                "userId": "",
                "name": "",
                "ownCardSelected": false,
                "tableCardSelected": false,
                "readyForNextRound": false,
                "dealer": false,
                "inactive": false,
                "gameScore": 0,
                "lastRoundScore": 0
            }],
            "table": [{
                "token": "",
                "source": "",
                "metadata": {
                    "author": "",
                    "externalUrl": "",
                    "description": ""
                },
                "playerSelections": [],
                "dealersCard": false,
                "cardSelectedBy": false
            }]
        },
        /* fields below are not part of model from backend */
        "chatHistory": [{
            "player": "",
            "message": "",
            "time": ""
        }],
        "chatInput": "",
        "tagsInput": "",
        "selectedCard": {
            "token": "",
            "source": "",
            "metadata": {
                "author": "",
                "externalUrl": "",
                "description": ""
            }
        }
    };
}
