'use strict';

module.exports = function (app) {
    app.use('/', () => {
        console.log('request');
    });

    return app;
};
