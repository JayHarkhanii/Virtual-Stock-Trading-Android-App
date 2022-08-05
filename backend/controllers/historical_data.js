const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_historical_data = async (req, res) => {
    const ticker_symbol = req.params.company_ticker

    const to_date = Math.round(new Date().getTime() / 1000)
    const curr_date = new Date();
    
    // Get the last 2 years data
    curr_date.setFullYear(curr_date.getFullYear() - 2);
    const from_date = Math.round(curr_date.getTime() / 1000);
    const historical_url = 'https://finnhub.io/api/v1/stock/candle?symbol=' + ticker_symbol + '&resolution=D&from=' + from_date + '&to=' + to_date + '&token=' + api_key
  
    try {
        const api_result = await axios.get(historical_url)
        res.json(api_result.data)
    } 
    catch (e) {
        // throw error
        const { response } = e
        console.log(response)
    }
}

module.exports = router;