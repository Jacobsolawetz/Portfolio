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
  ## Calculate the portfolio and benchmark means by factor
  .[, PORTFOLIO_MEAN := sum(STANDARDIZED_PORTOFLIO_WEIGHTS*FACTOR_VALUE), by = c("FACTOR_TYPE")] %>% 
  .[, BENCHMARK_MEAN := sum(STANDARDIZED_BENCHMARK_WEIGHTS*FACTOR_VALUE), by = c("FACTOR_TYPE")] %>% 
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
write.csv(Tilts, "Outputs/Tilts.csv", row.names = F)





#2) Separate the contributions to tilt, sort them, and output them to the outputs folder.

list_of_factors <- unique(SkylineData$FACTOR_TYPE)
for (fact in list_of_factors) {
  #for each factor specify the filename=
  file_name <- paste("Outputs/",fact,"_Contributions.csv", sep ="")
  FactorData <- SkylineData[SkylineData$FACTOR_TYPE == fact,]
  #only take records that were in the portfolio or the benchmark
  FactorData <- FactorData[FactorData$END_PORTFOLIO_WEIGHT_PCT > 0 | FactorData$END_BENCHMARK_WEIGHT >0,]
  Contributions <- data.frame(NAME = FactorData$NAME,
                              TICKER = FactorData$TICKER,
                              SEDOL = FactorData$SEDOL,
                              CUSIP = FactorData$CUSIP,
                              ISIN = FactorData$ISIN,
                              CONTRIBUTION_TO_TILT = FactorData$CONTRIBUTION_TO_TILT,
                              PORTFOLIO_WEIGHT = FactorData$STANDARDIZED_PORTOFLIO_WEIGHTS,
                              BENCHMARK_WEIGHT = FactorData$STANDARDIZED_BENCHMARK_WEIGHTS,
                              FACTOR_VALUE = FactorData$FACTOR_VALUE,
                              PORTFOLIO_MEAN = FactorData$PORTFOLIO_MEAN, 
                              BENCHMARK_MEAN = FactorData$BENCHMARK_MEAN,
                              UNIVERSE_SD = FactorData$UNIVERSE_SD,
                              ADJUSTMENT_FACTOR = FactorData$ADJUSTMENT_FACTOR)
  #now sort in order of contribution to tilt
  Contributions <- Contributions[order(-Contributions$CONTRIBUTION_TO_TILT),]
  #write the data to a csv
  
  write.csv(Contributions, file_name, row.names = F)
}



#3) Output the fianl data coverage for all factors

MissingDataAggregate <- read.csv("Staging/MissingDataAggregate.csv", header = TRUE, stringsAsFactors = FALSE)
UnmatchedDataAggregate <- read.csv("Staging/UnmatchedDataAggregate.csv", header = TRUE, stringsAsFactors = FALSE)
DataCoverage <- SkylineData[unique(SkylineData$FACTOR_TYPE), mult="first"]
DataCoverage <- data.frame(FACTOR_TYPE = DataCoverage$FACTOR_TYPE, 
                           PORTFOLIO_COVERAGE = DataCoverage$SUM_PORTFOLIO_WT,
                           UNMATCHED_PORTFOLIO_PCT = UnmatchedDataAggregate$Sum_END_PORTFOLIO_WEIGHT_PCT[1],
                           MISSING_PORTFOLIO_PCT = MissingDataAggregate$Sum_END_PORTFOLIO_WEIGHT_PCT,
                           TOTAL_PORTFOLIO_DATA = (DataCoverage$SUM_PORTFOLIO_WT + UnmatchedDataAggregate$Sum_END_PORTFOLIO_WEIGHT_PCT[1] +MissingDataAggregate$Sum_END_PORTFOLIO_WEIGHT_PCT),
                           BENCHMARK_COVERAGE = DataCoverage$SUM_BENCHMARK_WT,
                           UNMATCHED_BENCHMARK_PCT = UnmatchedDataAggregate$Sum_END_BENCHMARK_WEIGHT_PCT[1],
                           MISSING_BENCHMARK_PCT = MissingDataAggregate$Sum_END_BENCHMARK_WEIGHT_PCT,
                           TOTAL_BENCHMARK_DATA = (DataCoverage$SUM_BENCHMARK_WT + UnmatchedDataAggregate$Sum_END_BENCHMARK_WEIGHT_PCT[1] + MissingDataAggregate$Sum_END_BENCHMARK_WEIGHT_PCT))
write.csv(DataCoverage, "Outputs/Data_Coverage/Final_Coverage.csv", row.names = F)





#4) Output the missing data by factor

MissingData <- read.csv("Staging/MissingData.csv", header = TRUE, stringsAsFactors = FALSE)
list_of_factors <- unique(MissingData$FACTOR_TYPE)

for (fact in list_of_factors) {
  #for each factor specify the filename=
  file_name <- paste("Outputs/Data_Coverage/",fact,"_Missing_Data.csv", sep ="")
  Missing <- MissingData[MissingData$FACTOR_TYPE == fact,]
  #only take records that were in the portfolio or the benchmark
  Missing <- Missing[Missing$END_PORTFOLIO_WEIGHT_PCT > 0 | Missing$END_BENCHMARK_WEIGHT >0,]
  Missing <- data.frame(NAME = Missing$ISSUE_COMPANY_NM,
                              TICKER = Missing$TICKER,
                              SEDOL = Missing$SEDOL_NUM,
                              CUSIP = Missing$CUSIP_NUM,
                              ISIN = Missing$ISIN_NUM,
                              PORTFOLIO_WEIGHT = Missing$END_PORTFOLIO_WEIGHT_PCT,
                              BENCHMARK_WEIGHT = Missing$END_BENCHMARK_WEIGHT_PCT)
  #now sort in order of contribution to tilt
  Missing <- Missing[order(-Missing$PORTFOLIO_WEIGHT),]
  #write the data to a csv
  
  write.csv(Missing, file_name, row.names = F)
}






