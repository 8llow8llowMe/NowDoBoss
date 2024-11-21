import { useEffect } from 'react'
import analysisStore from '@src/stores/analysisStore'
import useAnalysisSummaryStore from '@src/stores/analysisSummaryStore'
import HalfDoughnutChart from '@src/common/HalfDoughnutChart'
import * as s from '@src/components/styles/analysis/result/StoreCountAnalysisStyle'

const FranchiseChart = () => {
  const selectedService = analysisStore(state => state.selectedService)
  const storeCountDataBody = analysisStore(state => state.storeCountDataBody)
  const setStoreSummary = useAnalysisSummaryStore(
    state => state.setStoreSummary,
  )

  const { normalStore, franchiseStore } =
    storeCountDataBody.franchiseStoreInfo

  // 차트 props
  const labels = ['일반', '프랜차이즈']
  const values = [normalStore, franchiseStore]

  // 요약 상태 업데이트
  useEffect(() => {
    let newTip
    const totalStores = normalStore + franchiseStore

    if (totalStores === 0) {
      newTip = `현재 상권에 유사한 사업체가 전혀 없습니다. 이는 새로운 사업을 시작하기에 매우 유리한 조건입니다. 시장에서 선도자가 될 좋은 기회를 가지고 있으니, 시장 조사를 철저히 하고 사업 계획을 세우십시오`
    } else if (franchiseStore === 0) {
      if (normalStore <= 7) {
        newTip = `해당 상권에는 일반 매장 ${normalStore}개만 존재하며, 프랜차이즈 매장은 없습니다. 시장 진입 또는 기존 사업의 확장에 매우 유리한 조건입니다. 이 비교적 낮은 경쟁 상황은 시장 점유율을 빠르게 확보할 좋은 기회를 제공합니다`
      } else {
        newTip = `해당 상권에는 일반 매장 ${normalStore}개가 있으며 프랜차이즈 매장은 없습니다. 경쟁은 치열하지만 프랜차이즈 확장의 잠재력은 상대적으로 높습니다. 프랜차이즈의 장점을 활용하는 차별화 전략을 개발하는 것이 중요합니다`
      }
    } else if (totalStores <= 5) {
      newTip = `유사한 업종 점포의 총합이 ${totalStores}개에 불과하여, 해당 상권은 신규 진입에 매우 유리한 상황입니다. 시장 리서치를 통해 타겟 고객층의 요구와 기대를 파악하는 것이 중요합니다`
    } else if (totalStores > 5 && totalStores <= 10) {
      if (franchiseStore <= 2) {
        newTip = `총 ${totalStores}개의 유사 업종 점포 중 프랜차이즈 점포가 ${franchiseStore}개에 불과하여, 프랜차이즈 확장에 유리한 조건을 갖추고 있습니다. 프랜차이즈의 브랜드 가치를 활용해 시장을 공략하세요`
      } else {
        newTip = `총 ${totalStores}개의 점포 중 프랜차이즈 점포가 ${franchiseStore}개로, 시장 내 프랜차이즈 비중이 높습니다. 이는 해당 상권에서 프랜차이즈의 강력한 시장 지배력을 의미하며, 차별화된 접근 방식이 필요합니다`
      }
    } else if (totalStores > 10 && totalStores <= 20) {
      if (franchiseStore < normalStore / 2) {
        newTip = `총 ${totalStores}개의 점포 중 프랜차이즈 점포 비율이 낮아, 프랜차이즈 확장의 기회가 있습니다. 시장 내 비프랜차이즈 점포의 강점과 약점을 분석하여 프랜차이즈의 경쟁 우위를 확보하세요`
      } else {
        newTip = `해당 상권은 총 ${totalStores}개의 점포 중 프랜차이즈 점포가 절반 이상을 차지하고 있어, 경쟁이 치열합니다. 프랜차이즈는 혁신적인 서비스나 제품으로 시장 내 경쟁력을 강화해야 합니다`
      }
    } else if (totalStores > 20) {
      if (franchiseStore <= 10) {
        newTip = `총 ${totalStores}개의 점포 중 프랜차이즈 점포가 ${franchiseStore}개로 상대적으로 낮아, 시장 내 프랜차이즈 확장의 잠재력이 있습니다. 다만, 고도의 시장 분석과 차별화 전략이 필요합니다`
      } else {
        newTip = `총 ${totalStores}개의 점포 중 프랜차이즈 점포가 ${franchiseStore}개로 프랜차이즈 시장이 포화 상태입니다. 이러한 환경에서는 기존 프랜차이즈와의 협업이나, 완전히 새로운 시장 세그먼트 개척이 필요할 수 있습니다`
      }
    } else {
      newTip = `해당 상권의 상황은 매우 복잡하여, 보다 세밀한 시장 분석과 전략이 필요합니다. 경쟁 상황, 고객 요구, 지역적 특성을 종합적으로 고려한 맞춤형 전략을 수립하세요`
    }

    setStoreSummary('tip', newTip)
  }, [normalStore, franchiseStore, setStoreSummary])

  return (
    <s.FranchiseChart>
      <s.ChartTitle>프랜차이즈 점포 수</s.ChartTitle>
      <s.ChartSubTitleWrap>
        <s.InfoText>{selectedService.serviceCodeName}의</s.InfoText>
        {franchiseStore > 0 ? (
          <>
            <s.InfoText>프랜차이즈 점포는</s.InfoText>
            <s.InfoText>
              <s.HighlightText>{franchiseStore}개</s.HighlightText> 있어요.
            </s.InfoText>
          </>
        ) : (
          <>
            <s.InfoText>프랜차이즈 점포는</s.InfoText>
            <s.InfoText>
              <s.HighlightText>없어요.</s.HighlightText>
            </s.InfoText>
          </>
        )}
      </s.ChartSubTitleWrap>
      <HalfDoughnutChart labels={labels} values={values} />
    </s.FranchiseChart>
  )
}

export default FranchiseChart
