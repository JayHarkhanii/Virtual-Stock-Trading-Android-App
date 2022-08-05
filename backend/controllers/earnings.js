const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_earnings = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    axios.get('https://finnhub.io/api/v1/stock/earnings/', {
        params: {
            symbol: ticker_symbol,
            token: api_key
        }
    })
        .then(function (response) {
            let earnings_data = {}
            
            earnings_data.actual = []
            earnings_data.estimate = []
            earnings_data.period = []

            for (let i = 0; i < response.data.length; i++) {
                earnings_data.period.push(response.data[i].period + '<br>Surprise: ' + response.data[i].surprise.toString())
                earnings_data.actual.push([i, response.data[i].actual])
                earnings_data.estimate.push([i, response.data[i].estimate])
            }

            res.send(earnings_data);
        })
        .catch(function (e) {
            // throw error
            const { response } = e
            console.log(response)
        })
}

module.exports = router;