const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_reccomendations = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    const recommendation_url = 'https://finnhub.io/api/v1/stock/recommendation?symbol=' + ticker_symbol + '&token=' + api_key

            axios.get('https://finnhub.io/api/v1/stock/recommendation/', {
                params: {
                  symbol: ticker_symbol,
                  token: api_key
                }
              })
              .then(function (response) {
                let reco_data = {}
                
                reco_data.buy = []
                reco_data.strongBuy = []
                reco_data.hold = []
                reco_data.strongSell = []
                reco_data.sell = []
                reco_data.period = []
                reco_data.symbol = response.data[0].symbol
        
                for(let i = 0; i < response.data.length; i++){
                  reco_data.buy.push(response.data[i].buy)
                  reco_data.strongBuy.push(response.data[i].strongBuy)
                  reco_data.hold.push(response.data[i].hold)
                  reco_data.strongSell.push(response.data[i].strongSell)
                  reco_data.sell.push(response.data[i].sell)
                  reco_data.period.push(response.data[i].period.slice(0,7))
                }
                res.send(reco_data);
              })
               .catch(function (e) {
                // throw error
                const { response } = e
                console.log(response)
              })
        
}

module.exports = router;