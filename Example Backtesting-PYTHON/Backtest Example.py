import tradingWithPython.lib.yahooFinance as yf
from tradingWithPython import sharpe
from tradingWithPython import candlestick
import pandas as pd

ohlc = yf.getHistoricData('XLU')[['open', 'high', 'low', 'close', 'volume']]
ohlc1 = yf.getHistoricData('AAPL')[['open', 'high', 'low', 'close', 'volume']]
ohlc2 = yf.getHistoricData('GS')[['open', 'high', 'low', 'close', 'volume']]
ohlc['close'].plot()

#bollinger bands squeeze that is the difference is less one half the rolling mean diff , rsi (5) or rsi (2) > 70 then overbought and sell. if < 30 then oversold and buy

def bbands(price, length=30, numsd=2):
    """ returns average, upper band, and lower band"""
    ave = pd.stats.moments.rolling_mean(price,length)
    sd = pd.stats.moments.rolling_std(price,length)
    upband = ave + (sd*numsd)
    dnband = ave - (sd*numsd)
    return np.round(ave,3), np.round(upband,3), np.round(dnband,3)

bollinger = pd.DataFrame(index=ohlc.index)
bollinger['ave'], bollinger['upper'], bollinger['lower'] = bbands(ohlc.close, length=30, numsd=1)
bollinger[-200:].plot()
ohlc['close'][-200:].plot()

idx = (bollinger['diff']<(bollinger['rolling_mean'])-bollinger['rolling_std'])

def relative_strength(prices, n=14):
    """
    compute the n period relative strength indicator
    """

    deltas = np.diff(prices)
    seed = deltas[:n+1]
    up = seed[seed>=0].sum()/n
    down = -seed[seed<0].sum()/n
    rs = up/down
    rsi = np.zeros_like(prices)
    rsi[:n] = 100. - 100./(1.+rs)

    for i in range(n, len(prices)):
        delta = deltas[i-1] # cause the diff is 1 shorter

        if delta>0:
            upval = delta
            downval = 0.
        else:
            upval = 0.
            downval = -delta

        up = (up*(n-1) + upval)/n
        down = (down*(n-1) + downval)/n

        rs = up/down
        rsi[i] = 100. - 100./(1.+rs)

    return rsi
    

def backtest(ohlc, squeezeThresh = .5, rsiThresh = 60):
    #set up the strategy data
    stratData = pd.DataFrame(index = ohlc.index)
    stratData['ret'] = 100*ohlc['close'].pct_change()
    stratData['ret'][0] = 0
    stratData['rsi'] = relative_strength(ohlc.close, n=14)
    stratData['bollinger_ave'], stratData['bollinger_upper'], stratData['bollinger_lower'] = bbands(ohlc.close, length=30, numsd=1)
    stratData['bollinger_diff'] = stratData['bollinger_upper'] - stratData['bollinger_lower']
    s = stratData['bollinger_diff']
    stratData['rolling_mean'] =  pd.stats.moments.rolling_mean(s,30)
    stratData['rolling_std'] = pd.stats.moments.rolling_std(s,30)
    #construct a trigger index to go short, bollinger squeezes and the rsi is high
    idx = (stratData['bollinger_diff'] < (stratData['rolling_mean']-squeezeThresh*stratData['rolling_std'])) & (stratData['rsi'] > rsiThresh)
    idx[0] = False
    
    stratData['pnl'] = 0
    #long and hold for 1 da
    stratData['pnl'][idx] = stratData['ret'].shift(-1)[idx] #need to think about if it's ret or ret.shift(-1) being the next day
    for i, item in enumerate(stratData['pnl']):
        if math.isnan(item):
            stratData[i] = 0
    return stratData['pnl']
    
    
pnl = backtest(ohlc) #backtesting with normal parameters
pnl.cumsum().plot()
print 'Sharpe', sharpe(pnl)


##here is an optimization of the trading strategy in a linear space
import matplotlib.pyplot as plt
squeezeThresh = np.linspace(-2,2,30)
rsiThresh = np.linspace(0, 80, 30)

SH = np.zeros((len(squeezeThresh), len(rsiThresh)))

for i, squeeze in enumerate(squeezeThresh):
    for j, rsi in enumerate(rsiThresh):
        pnl = backtest(ohlc, squeezeThresh = squeeze , rsiThresh = rsi)
        if sharpe(pnl) > 0:
            SH[i,j] = sharpe(pnl)
        else:
            SH[i,j] = 0
    
plt.pcolor(squeezeThresh, rsiThresh, SH)
plt.colorbar();

