#Load in necessary packages
usePackage <- function(p) {
  if (!is.element(p, installed.packages()[,1]))
    install.packages(p, dep = TRUE)
  require(p, character.only = TRUE)
}

##here is more than an exhaustive list of the packages we will need
requiredPackages <- c('lubridate','reshape2','tidyr','dplyr','data.table', 'magrittr','stringr')
for(package in requiredPackages) {usePackage(package)} 

##Here we load in the necessary statistic calulations we'll need. 
source("Calculations/Weighted_Stats.R")

#Load in data that has been staged in Alteryx
SkylineData <- read.csv("Staging/AllSkylineData.csv", header = TRUE, stringsAsFactors = FALSE) %>% 
  data.table %>%
  setkey(FACTOR_TYPE) %>%
  ## Standardize the weights to sum to 1 and to re balance weights due to missing data
  .[, SUM_PORTFOLIO_WT := sum(END_PORTFOLIO_WEIGHT_PCT), by = c("FACTOR_TYPE")] %>%
  .[, SUM_BENCHMARK_WT := sum(END_BENCHMARK_WEIGHT), by = c("FACTOR_TYPE")] %>%
  .[, SUM_UNIVERSE_WT := sum(UNIVERSE_WEIGHT), by = c("FACTOR_TYPE")] %>%
  .[, STANDARDIZED_PORTOFLIO_WEIGHTS := (END_PORTFOLIO_WEIGHT_PCT / SUM_PORTFOLIO_WT)] %>%
  .[, STANDARDIZED_BENCHMARK_WEIGHTS := (END_BENCHMARK_WEIGHT / SUM_BENCHMARK_WT)] %>%
  .[, STANDARDIZED_UNIVERSE_WEIGHTS := (UNIVERSE_WEIGHT / SUM_UNIVERSE_WT)] %>%
  ##calculate the average sector factor value to adjust factor values, first by factor_type, then by sector. A multi date skyline would work similarly
  .[, TOTAL_SECTOR_MARKET_CAP := sum(UNIVERSE_WEIGHT), by = c("FACTOR_TYPE", "SECTOR")] %>%
  .[, TOTAL_SECTOR_FACTOR_VALUE := sum(UNIVERSE_WEIGHT*FACTOR_VALUE), by = c("FACTOR_TYPE", "SECTOR")] %>% 
  .[, AVERAGE_SECTOR_FACTOR_VALUE := (TOTAL_SECTOR_FACTOR_VALUE / TOTAL_SECTOR_MARKET_CAP)] %>%
  .[, SECTOR_ADJ_FACTOR_VALUE := (FACTOR_VALUE - AVERAGE_SECTOR_FACTOR_VALUE)] %>% 
  ## Calculate the portfolio and benchmark means by factor
  .[, PORTFOLIO_MEAN := sum(STANDARDIZED_PORTOFLIO_WEIGHTS*SECTOR_ADJ_FACTOR_VALUE), by = c("FACTOR_TYPE")] %>% 
  .[, BENCHMARK_MEAN := sum(STANDARDIZED_BENCHMARK_WEIGHTS*SECTOR_ADJ_FACTOR_VALUE), by = c("FACTOR_TYPE")] %>% 
  ## Calculate the Adjustment factor and universe standard deviation by factor
  .[, ADJUSTMENT_FACTOR := sqrt(sum(STANDARDIZED_PORTOFLIO_WEIGHTS*STANDARDIZED_PORTOFLIO_WEIGHTS)), by = c("FACTOR_TYPE")] %>% 
  .[, UNIVERSE_SD := wt.sd(FACTOR_VALUE, STANDARDIZED_UNIVERSE_WEIGHTS), by = c("FACTOR_TYPE")] %>% 
  .[, TILT := (PORTFOLIO_MEAN - BENCHMARK_MEAN)/(ADJUSTMENT_FACTOR*UNIVERSE_SD)] %>% 
  ## Calculat ethe contribution to Tilt of individual securites. 
  .[, CONTRIBUTION_TO_TILT := (STANDARDIZED_PORTOFLIO_WEIGHTS - STANDARDIZED_BENCHMARK_WEIGHTS)*(FACTOR_VALUE - BENCHMARK_MEAN)/ (UNIVERSE_SD*ADJUSTMENT_FACTOR)] 

##Pull out the Tilts by factor for the data set
Tilts <- SkylineData[unique(SkylineData$FACTOR_TYPE), mult="first"]

##Set up the data to be plotted
Tilts <- data.frame(FACTOR_TYPE = factor(Tilts$FACTOR_TYPE, levels = unique(SkylineData$FACTOR_TYPE)), TILT = Tilts$TILT)

#Skyline Product plotting
require(ggplot2)
require('ggthemes')
#Plot the skyline
ggplot(data=Tilts, aes(x=FACTOR_TYPE,y=TILT)) +#, color = FACTOR_TYPE, fill = FACTOR_TYPE)) +
  geom_bar(stat="identity") + #+ theme_economist() 
  ggtitle('Portfolio Style Skyline - S01-S&P500') #+
#scale_colour_economist() + scale_fill_economist()


#-------------------Output Products.----------------------#

#1)Out put the tilts page
write.csv(Tilts, "Outputs/Sector_Adjusted_Tilts.csv", row.names = F)



#2) Output the average sector factor values

SkylineData %>% 
  setkey(FACTOR_TYPE, SECTOR)

