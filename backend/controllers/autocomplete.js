const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'


// Autocomple API
router.autoComplete_data = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    const autocomplete_url = 'https://finnhub.io/api/v1/search?q=' + ticker_symbol + '&token=' + api_key
    
    try {
        const api_result = await axios.get(autocomplete_url)
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