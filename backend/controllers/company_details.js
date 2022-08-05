const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_details_data = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    const company_details_url = 'https://finnhub.io/api/v1/stock/profile2?symbol=' + ticker_symbol + '&token=' + api_key
    
    try {
        const api_result = await axios.get(company_details_url)
        // console.log(api_result.data)
        res.json(api_result.data)
    } 
    catch (e) {
        // throw error
        const { response } = e
        console.log(response)
    }
}

module.exports = router;