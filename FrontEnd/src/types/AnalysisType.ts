import React from 'react'

// data type - 업종
export type SelectedServiceType = {
  serviceCode: string
  serviceCodeName: string
  serviceType: string
}

// data type - 유동인구
export type TimeSlotFootTrafficType = {
  footTraffic00: number
  footTraffic06: number
  footTraffic11: number
  footTraffic14: number
  footTraffic17: number
  footTraffic21: number
}

export type DayOfWeekFootTrafficType = {
  monFootTraffic: number
  tueFootTraffic: number
  wedFootTraffic: number
  thuFootTraffic: number
  friFootTraffic: number
  satFootTraffic: number
  sunFootTraffic: number
}

export type AgeGroupFootTrafficType = {
  teenFootTraffic: number
  twentyFootTraffic: number
  thirtyFootTraffic: number
  fortyFootTraffic: number
  fiftyFootTraffic: number
  sixtyFootTraffic: number
}

export type AeGenderPercentFootTrafficType = {
  maleTeenFootTrafficPercent: number
  femaleTeenFootTrafficPercent: number
  maleTwentyFootTrafficPercent: number
  femaleTwentyFootTrafficPercent: number
  maleThirtyFootTrafficPercent: number
  femaleThirtyFootTrafficPercent: number
  maleFortyFootTrafficPercent: number
  femaleFortyFootTrafficPercent: number
  maleFiftyFootTrafficPercent: number
  femaleFiftyFootTrafficPercent: number
  maleSixtyFootTrafficPercent: number
  femaleSixtyFootTrafficPercent: number
}

export type FlowPopulationDataBodyType = {
  timeSlotFootTraffic: TimeSlotFootTrafficType
  dayOfWeekFootTraffic: DayOfWeekFootTrafficType
  ageGroupFootTraffic: AgeGroupFootTrafficType
  ageGenderPercentFootTraffic: AeGenderPercentFootTrafficType
}

// data type - 매출분석
export type TimeSalesInfoType = {
  sales00: number
  sales06: number
  sales11: number
  sales14: number
  sales17: number
  sales21: number
}

export type DaySalesInfoType = {
  monSales: number
  tueSales: number
  wedSales: number
  thuSales: number
  friSales: number
  satSales: number
  sunSales: number
}

export type AgeSalesInfoType = {
  teenSales: number
  twentySales: number
  thirtySales: number
  fortySales: number
  fiftySales: number
  sixtySales: number
}

export type AgeGenderPercentSalesType = {
  maleTeenSalesPercent: number
  femaleTeenSalesPercent: number
  maleTwentySalesPercent: number
  femaleTwentySalesPercent: number
  maleThirtySalesPercent: number
  femaleThirtySalesPercent: number
  maleFortySalesPercent: number
  femaleFortySalesPercent: number
  maleFiftySalesPercent: number
  femaleFiftySalesPercent: number
  maleSixtySalesPercent: number
  femaleSixtySalesPercent: number
}

export type DaySalesCountInfoType = {
  monSalesCount: number
  tueSalesCount: number
  wedSalesCount: number
  thuSalesCount: number
  friSalesCount: number
  satSalesCount: number
  sunSalesCount: number
}

export type TimeSalesCountInfoType = {
  salesCount00: number
  salesCount06: number
  salesCount11: number
  salesCount14: number
  salesCount17: number
  salesCount21: number
}

export type GenderSalesCountInfoType = {
  maleSalesCount: number
  femaleSalesCount: number
}

export type AnnualQuarterSalesInfoType = {
  periodCode: string
  totalSales: number
}

export type SalesDataBodyType = {
  timeSalesInfo: TimeSalesInfoType
  daySalesInfo: DaySalesInfoType
  ageSalesInfo: AgeSalesInfoType
  ageGenderPercentSales: AgeGenderPercentSalesType
  daySalesCountInfo: DaySalesCountInfoType
  timeSalesCountInfo: TimeSalesCountInfoType
  genderSalesCountInfo: GenderSalesCountInfoType
  annualQuarterSalesInfos: AnnualQuarterSalesInfoType[]
}

// data type - 매출분석 (매출 총 금액)
export type DistrictTotalSalesInfoType = {
  districtCode: string
  districtCodeName: string
  totalSales: number
}

export type AdministrationTotalSalesInfoType = {
  administrationCode: string
  administrationCodeName: string
  totalSales: number
}

export type CommercialTotalSalesInfoType = {
  commercialCode: string
  commercialCodeName: string
  totalSales: number
}

export type TotalSalesDataBodyType = {
  districtTotalSalesInfo: DistrictTotalSalesInfoType
  administrationTotalSalesInfo: AdministrationTotalSalesInfoType
  commercialTotalSalesInfo: CommercialTotalSalesInfoType
}

// data type - 점포 수
export type SameStoreInfoType = {
  serviceCodeName: string
  totalStore: number
}

export type FranchiseStoreInfoType = {
  normalStore: number
  franchiseStore: number
  normalStorePercentage: number
  franchisePercentage: number
}

export type OpenAndCloseStoreInfoType = {
  openedRate: number
  closedRate: number
}

export type StoreCountDataBodyType = {
  sameStoreInfos: SameStoreInfoType[]
  sameTotalStore: number
  franchiseStoreInfo: FranchiseStoreInfoType
  openAndCloseStoreInfo: OpenAndCloseStoreInfoType
}

// data type - 상주인구
export type PopulationInfoType = {
  totalPopulation: number
  teenPopulation: number
  twentyPopulation: number
  thirtyPopulation: number
  fortyPopulation: number
  fiftyPopulation: number
  sixtyPopulation: number
}

export type ResidentPopulationDataBodyType = {
  populationInfo: PopulationInfoType
  malePercentage: number
  femalePercentage: number
}

// data type - 지출내역
export type AvgIncomeInfoType = {
  monthAvgIncome: number
  incomeSectionCode: number
}

export type AnnualQuarterIncomeInfoType = {
  periodCode: string
  totalPrice: number
}

export type TypeIncomeInfoType = {
  groceryPrice: number
  clothesPrice: number
  medicalPrice: number
  lifePrice: number
  trafficPrice: number
  leisurePrice: number
  culturePrice: number
  educationPrice: number
  luxuryPrice: number
}

export type ExpenditureDataBodyType = {
  avgIncomeInfo: AvgIncomeInfoType
  annualQuarterIncomeInfos: AnnualQuarterIncomeInfoType[]
  typeIncomeInfo: TypeIncomeInfoType
}

// data type - 지출내역 (지출 총 금액)
export type DistrictTotalIncomeInfoType = {
  districtCode: string
  districtCodeName: string
  totalPrice: number
}

export type AdministrationTotalIncomeInfoType = {
  administrationCode: string
  administrationCodeName: string
  totalPrice: number
}

export type CommercialTotalIncomeInfoType = {
  commercialCode: string
  commercialCodeName: string
  totalPrice: number
}

export type TotalExpenditureDataBodyType = {
  districtTotalIncomeInfo: DistrictTotalIncomeInfoType
  administrationTotalIncomeInfo: AdministrationTotalIncomeInfoType
  commercialTotalIncomeInfo: CommercialTotalIncomeInfoType
}

// data type - 북마크
export type AnalysisBookmarksDataType = {
  districtCode: string
  districtCodeName: string
  administrationCode: string
  administrationCodeName: string
  commercialCode: string
  commercialCodeName: string
  serviceType: string
  serviceCode: string
  serviceCodeName: string
}

// 북마크 반환 데이터
export type AnalysisBookmarksResDataType = {
  districtCode: string
  districtCodeName: string
  administrationCode: string
  administrationCodeName: string
  commercialCode: string
  commercialCodeName: string
  serviceType: string
  serviceCode: string
  serviceCodeName: string
  createdAt: string
}

// props type
export type SelectContainerPropsType = {
  setIsReady: React.Dispatch<React.SetStateAction<boolean>>
  handleResultButtonClick: () => void
}

export type CategoryTitleCardProps = {
  src: string
  title: string
  setPeriodCode: React.Dispatch<React.SetStateAction<string>>
}

export type SearchSectionPropsType = {
  isOpen: boolean
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>
  handleResultButtonClick: () => void
}

export type ChoiceServicePropsType = {
  handleServiceTypeClick: (serviceType: string) => void
}

export type ChoiceServiceDetailPropsType = {
  serviceList: SelectedServiceType[]
}

export type ResultIntroPropsType = {
  handlePostAnalysisBookmarks: () => void
}

// store type
export type AnalysisStoreType = {
  selectedServiceType: string // 선택한 업종 대분류
  selectedService: SelectedServiceType // 선택한 업종 소분류
  serviceDataBody: SelectedServiceType[] // 업종 API 반환 데이터
  flowPopulationDataBody: FlowPopulationDataBodyType // 유동인구 API 반환 데이터
  salesDataBody: SalesDataBodyType // 매출분석 API 반환 데이터
  totalSalesDataBody: TotalSalesDataBodyType // 매출분석 (매출 총 금액) API 반환 데이터
  storeCountDataBody: StoreCountDataBodyType // 점포수 API 반환 데이터
  residentPopulationDataBody: ResidentPopulationDataBodyType // 상주인구 API 반환 데이터
  expenditureDataBody: ExpenditureDataBodyType // 지출내역 API 반환 데이터
  totalExpenditureDataBody: TotalExpenditureDataBodyType // 지출내역 (지출 총 금액) API 반환 데이터
  setSelectedServiceType: (serviceType: string) => void
  setSelectedService: (service: SelectedServiceType) => void
  setServiceDataBody: (dataBody: SelectedServiceType[]) => void
  setFlowPopulationDataBody: (dataBody: FlowPopulationDataBodyType) => void
  setSalesDataBody: (dataBody: SalesDataBodyType) => void
  setTotalSalesDataBody: (dataBody: TotalSalesDataBodyType) => void
  setStoreCountDataBody: (dataBody: StoreCountDataBodyType) => void
  setResidentPopulationDataBody: (
    dataBody: ResidentPopulationDataBodyType,
  ) => void
  setExpenditureDataBody: (dataBody: ExpenditureDataBodyType) => void
  setTotalExpenditureDataBody: (dataBody: TotalExpenditureDataBodyType) => void
}
