const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_daily_data = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    const date = req.params.to_date;

    const curr_date = parseInt(date)
    console.log(curr_date, typeof(curr_date))
    const daily_url = 'https://finnhub.io/api/v1/stock/candle?symbol=' + ticker_symbol + '&resolution=5&from=' + parseInt(curr_date - (6 * 3600)) + '&to=' + parseInt(curr_date) + '&token=' + api_key
    
    try {
        const api_result = await axios.get(daily_url)
        res.json(api_result.data)
    } 
    catch (e) {
        // throw error
        const { response } = e
        console.log(response)
    }
}

module.exports = router;