import * as m from '@src/containers/main/MainContainerStyle'
import * as r from '@src/containers/main/MainRecommendContainerStyle'
import { useNavigate } from 'react-router-dom'
import ContainerBox from '@src/common/ContainerBox'
import MainBarChart2 from '@src/common/MainBarChart2'
import MainBarChart3 from '@src/common/MainBarChart3'

const MainRecommendContainer = () => {
  const navigate = useNavigate()

  return (
    <r.Container>
      <r.Content>
        <r.Text>
          <m.BlueText>Market Recommend Report</m.BlueText>
          <m.Title>상권 추천 보고서</m.Title>
          <m.TextContent>
            지도에서 원하는 지역을 선택, <br />
            창업 조건에 유리한 상권을 <br />
            지금 바로 추천받아 보세요 <br />
          </m.TextContent>
          <m.GoButton onClick={() => navigate('/recommend')}>
            추천받으러 가기 &nbsp;&nbsp;
            <m.BannerArrow>→</m.BannerArrow>{' '}
          </m.GoButton>
        </r.Text>
        <r.CardList>
          <r.Page>
            <r.Card>
              <r.HeaderText>타 상권 대비 유동인구</r.HeaderText>
              <r.TitleText>추천 상권 유동인구 476만명</r.TitleText>
              <r.SubTitleText>380만명 더 많습니다</r.SubTitleText>
              <ContainerBox height={150} />
              <r.RowDiv>
                <r.CardImg src="images/threeChartImg.png" />
              </r.RowDiv>
            </r.Card>
          </r.Page>
          <r.Page>
            <r.Card>
              <r.HeaderText>타 상권 대비 총 매출</r.HeaderText>
              <r.TitleText>추천 상권 총 매출 280억</r.TitleText>
              <r.SubTitleText>150억 더 많습니다</r.SubTitleText>
              <ContainerBox height={100} />
              <r.Degree>
                <MainBarChart3
                  labels={['', '', '', '', '', '']}
                  values={[45, 55, 60, 50, 45, 38, 35]}
                />
              </r.Degree>
            </r.Card>
          </r.Page>
          <r.Page>
            <r.Card>
              <r.HeaderText>블루오션</r.HeaderText>
              <r.TitleText>
                주변 상권에 많지만 <br /> 해당상권에 적은 업종 제안
              </r.TitleText>
              <r.SubTitleText>380만명 더 많습니다</r.SubTitleText>
              <ContainerBox height={120} />
              <MainBarChart2
                labels={['', '', '', '', '', '']}
                values={[60, 55, 50, 45, 35, 30, 25]}
              />
            </r.Card>
          </r.Page>
        </r.CardList>
      </r.Content>
    </r.Container>
  )
}

export default MainRecommendContainer
