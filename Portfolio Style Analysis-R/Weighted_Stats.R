wt.mean <- function(x,wt) {
  s = which(is.finite(x*wt)); wt = wt[s]; x = x[s] #remove NA info
  return( sum(wt * x)/sum(wt) ) #return the mean
}
wt.var <- function(x,wt) {
  s = which(is.finite(x + wt)); wt = wt[s]; x = x[s] #remove NA info
  xbar = wt.mean(x,wt) #get the weighted mean
  return( sum(wt *(x-xbar)^2)*(sum(wt)/(sum(wt)^2-sum(wt^2))) ) #return the variance
} 
wt.sd <- function(x,wt) { 
  return( sqrt(wt.var(x,wt)) ) #return the standard deviation
} 