const express = require('express');
const router = express.Router();

// const {company_daily_data, company_details_data, company_peers, company_news, company_latest_price, company_reccomendations, company_historical_data, company_sentiments, company_earnings} = require('../controllers/finnhub_controller')
const {autoComplete_data} = require('../controllers/autocomplete');
const {company_details_data} = require('../controllers/company_details');
const {company_daily_data} = require('../controllers/daily_data');
const {company_news} = require('../controllers/company_news');
const {company_peers} = require('../controllers/company_peers');
const {company_latest_price} = require('../controllers/latest_price');
const {company_reccomendations} = require('../controllers/recommendations');
const {company_historical_data} = require('../controllers/historical_data');
const {company_sentiments} = require('../controllers/sentiments');
const {company_earnings} = require('../controllers/earnings')


router.route('/autoComplete/:company_ticker').get(autoComplete_data);

router.route('/autoComplete/:company_ticker').get(autoComplete_data);

router.route('/company_daily_data/:company_ticker/:to_date').get(company_daily_data);

router.route('/company_historical_data/:company_ticker').get(company_historical_data);

router.route('/latest_price/:company_ticker').get(company_latest_price);

router.route('/company_details/:company_ticker').get(company_details_data);

router.route('/company_peers/:company_ticker').get(company_peers);

router.route('/complete_news/:company_ticker').get(company_news);

router.route('/recommendation/:company_ticker').get(company_reccomendations);

router.route('/sentiments/:company_ticker').get(company_sentiments);
    
router.route('/company_earnings/:company_ticker').get(company_earnings);


module.exports = router;