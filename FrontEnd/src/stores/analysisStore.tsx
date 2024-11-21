import { create } from 'zustand'
import {
  AnalysisStoreType,
  ExpenditureDataBodyType,
  FlowPopulationDataBodyType,
  ResidentPopulationDataBodyType,
  SalesDataBodyType,
  SelectedServiceType,
  StoreCountDataBodyType,
  TotalExpenditureDataBodyType,
  TotalSalesDataBodyType,
} from '@src/types/AnalysisType'

// 초기 상태 - 업종
const initialSelectService: SelectedServiceType = {
  serviceCode: '',
  serviceCodeName: '',
  serviceType: '',
}

const initialServiceDataBody: SelectedServiceType[] = []

// 초기 상태 - 유동인구
const initialFlowPopulationDataBody: FlowPopulationDataBodyType = {
  timeSlotFootTraffic: {
    footTraffic00: 0,
    footTraffic06: 0,
    footTraffic11: 0,
    footTraffic14: 0,
    footTraffic17: 0,
    footTraffic21: 0,
  },
  dayOfWeekFootTraffic: {
    monFootTraffic: 0,
    tueFootTraffic: 0,
    wedFootTraffic: 0,
    thuFootTraffic: 0,
    friFootTraffic: 0,
    satFootTraffic: 0,
    sunFootTraffic: 0,
  },
  ageGroupFootTraffic: {
    teenFootTraffic: 0,
    twentyFootTraffic: 0,
    thirtyFootTraffic: 0,
    fortyFootTraffic: 0,
    fiftyFootTraffic: 0,
    sixtyFootTraffic: 0,
  },
  ageGenderPercentFootTraffic: {
    maleTeenFootTrafficPercent: 0,
    femaleTeenFootTrafficPercent: 0,
    maleTwentyFootTrafficPercent: 0,
    femaleTwentyFootTrafficPercent: 0,
    maleThirtyFootTrafficPercent: 0,
    femaleThirtyFootTrafficPercent: 0,
    maleFortyFootTrafficPercent: 0,
    femaleFortyFootTrafficPercent: 0,
    maleFiftyFootTrafficPercent: 0,
    femaleFiftyFootTrafficPercent: 0,
    maleSixtyFootTrafficPercent: 0,
    femaleSixtyFootTrafficPercent: 0,
  },
}

// 초기 상태 - 매출분석
export const initialSalesDataBody: SalesDataBodyType = {
  timeSalesInfo: {
    sales00: 0,
    sales06: 0,
    sales11: 0,
    sales14: 0,
    sales17: 0,
    sales21: 0,
  },
  daySalesInfo: {
    monSales: 0,
    tueSales: 0,
    wedSales: 0,
    thuSales: 0,
    friSales: 0,
    satSales: 0,
    sunSales: 0,
  },
  ageSalesInfo: {
    teenSales: 0,
    twentySales: 0,
    thirtySales: 0,
    fortySales: 0,
    fiftySales: 0,
    sixtySales: 0,
  },
  ageGenderPercentSales: {
    maleTeenSalesPercent: 0,
    femaleTeenSalesPercent: 0,
    maleTwentySalesPercent: 0,
    femaleTwentySalesPercent: 0,
    maleThirtySalesPercent: 0,
    femaleThirtySalesPercent: 0,
    maleFortySalesPercent: 0,
    femaleFortySalesPercent: 0,
    maleFiftySalesPercent: 0,
    femaleFiftySalesPercent: 0,
    maleSixtySalesPercent: 0,
    femaleSixtySalesPercent: 0,
  },
  daySalesCountInfo: {
    monSalesCount: 0,
    tueSalesCount: 0,
    wedSalesCount: 0,
    thuSalesCount: 0,
    friSalesCount: 0,
    satSalesCount: 0,
    sunSalesCount: 0,
  },
  timeSalesCountInfo: {
    salesCount00: 0,
    salesCount06: 0,
    salesCount11: 0,
    salesCount14: 0,
    salesCount17: 0,
    salesCount21: 0,
  },
  genderSalesCountInfo: {
    maleSalesCount: 0,
    femaleSalesCount: 0,
  },
  annualQuarterSalesInfos: [],
}

// 초기 상태 - 매출분석 (매출 총 금액)
export const initialTotalSalesDataBody: TotalSalesDataBodyType = {
  districtTotalSalesInfo: {
    districtCode: '',
    districtCodeName: '',
    totalSales: 0,
  },
  administrationTotalSalesInfo: {
    administrationCode: '',
    administrationCodeName: '',
    totalSales: 0,
  },
  commercialTotalSalesInfo: {
    commercialCode: '',
    commercialCodeName: '',
    totalSales: 0,
  },
}

// 초기 상태 - 점포 수
const initialStoreCountDataBody: StoreCountDataBodyType = {
  sameStoreInfos: [],
  sameTotalStore: 0,
  franchiseStoreInfo: {
    normalStore: 0,
    franchiseStore: 0,
    normalStorePercentage: 0,
    franchisePercentage: 0,
  },
  openAndCloseStoreInfo: {
    openedRate: 0,
    closedRate: 0,
  },
}

// 초기 상태 - 상주인구
const initialResidentPopulationDataBody: ResidentPopulationDataBodyType = {
  populationInfo: {
    totalPopulation: 0,
    teenPopulation: 0,
    twentyPopulation: 0,
    thirtyPopulation: 0,
    fortyPopulation: 0,
    fiftyPopulation: 0,
    sixtyPopulation: 0,
  },
  malePercentage: 0,
  femalePercentage: 0,
}

// 초기 상태 - 지출 내역
const initialExpenditureDataBody: ExpenditureDataBodyType = {
  avgIncomeInfo: {
    monthAvgIncome: 0,
    incomeSectionCode: 0,
  },
  annualQuarterIncomeInfos: [],
  typeIncomeInfo: {
    groceryPrice: 0,
    clothesPrice: 0,
    medicalPrice: 0,
    lifePrice: 0,
    trafficPrice: 0,
    leisurePrice: 0,
    culturePrice: 0,
    educationPrice: 0,
    luxuryPrice: 0,
  },
}

// 초기 상태 - 지출 내역 (지출 총 금액)
const initialTotalExpenditureDataBody: TotalExpenditureDataBodyType = {
  districtTotalIncomeInfo: {
    districtCode: '',
    districtCodeName: '',
    totalPrice: 0,
  },
  administrationTotalIncomeInfo: {
    administrationCode: '',
    administrationCodeName: '',
    totalPrice: 0,
  },
  commercialTotalIncomeInfo: {
    commercialCode: '',
    commercialCodeName: '',
    totalPrice: 0,
  },
}

// store
const analysisStore = create<AnalysisStoreType>(set => ({
  selectedServiceType: '', // 선택한 업종 대분류
  selectedService: initialSelectService, // 선택한 업종 소분류
  serviceDataBody: initialServiceDataBody, // 업종 API 반환 데이터
  flowPopulationDataBody: initialFlowPopulationDataBody, // 유동인구 API 반환 데이터
  salesDataBody: initialSalesDataBody, // 매출분석 API 반환 데이터
  totalSalesDataBody: initialTotalSalesDataBody, // 매출분석 (매출 총 금액) API 반환 데이터
  storeCountDataBody: initialStoreCountDataBody, // 점포수 API 반환 데이터
  residentPopulationDataBody: initialResidentPopulationDataBody, // 상주인구 API 반환 데이터
  expenditureDataBody: initialExpenditureDataBody, // 지출내역 API 반환 데이터
  totalExpenditureDataBody: initialTotalExpenditureDataBody, // 지출내역 (총 지출 금액) API 반환 데이터
  setSelectedServiceType: serviceType =>
    set({ selectedServiceType: serviceType }),
  setSelectedService: service => set(() => ({ selectedService: service })),
  setServiceDataBody: dataBody => set({ serviceDataBody: dataBody }),
  setFlowPopulationDataBody: dataBody =>
    set({ flowPopulationDataBody: dataBody }),
  setSalesDataBody: dataBody => set({ salesDataBody: dataBody }),
  setTotalSalesDataBody: dataBody => set({ totalSalesDataBody: dataBody }),
  setStoreCountDataBody: dataBody => set({ storeCountDataBody: dataBody }),
  setResidentPopulationDataBody: dataBody =>
    set({ residentPopulationDataBody: dataBody }),
  setExpenditureDataBody: dataBody => set({ expenditureDataBody: dataBody }),
  setTotalExpenditureDataBody: dataBody =>
    set({ totalExpenditureDataBody: dataBody }),
}))

export default analysisStore
