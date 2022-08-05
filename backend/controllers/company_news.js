const axios = require('axios');
const router = require('../routes/routes');

const api_key = 'c867ivqad3i9fvji2mk0'

router.company_news = async (req, res) => {
    const ticker_symbol = req.params.company_ticker
    const curr_date = new Date();

    let day = curr_date.getDate();
    let month = curr_date.getMonth() + 1; 
    let year = curr_date.getFullYear();

    if (day < 10){ 
        day = '0' + day;
    }

    if (month < 10){
        month = '0' + month;
    }

    const formatted_to_date = year + '-' + month + '-' + day;

    var from = new Date(curr_date.getTime() - (7 * 24 * 60 * 60 * 1000));
    
    date = from.getDate();
    month = from.getMonth() + 1; 
    year = from.getFullYear();

    if (date < 10){ 
        date = '0' + date;
    }
    if (month < 10){ 
        month = '0' + month;
    }
    const formatted_from_date = year + '-' + month + '-' + date;
    const news_url = 'https://finnhub.io/api/v1/company-news?symbol=' + ticker_symbol + '&from=' + formatted_from_date +'&to='+ formatted_to_date +'&token=' + api_key

    try {
        const api_result = await axios.get(news_url)
        res.json(api_result.data)
    } 
    catch (e) {
        // throw error
        const { response } = e
        console.log(response)
    }
}

module.exports = router;