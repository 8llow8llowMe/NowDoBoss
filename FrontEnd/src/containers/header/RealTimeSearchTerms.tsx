import { useCallback, useEffect, useState } from 'react'
import { useTransition, animated } from 'react-spring'
import styled from 'styled-components'
import { useQuery } from '@tanstack/react-query'
import { fetchKorean } from '@src/api/mapApi'
import { useNavigate } from 'react-router-dom'
import useSelectPlaceStore from '@src/stores/selectPlaceStore'

const Container = styled.div`
  position: relative;
  //overflow: hidden;
  height: 30px;
  width: 280px; // 원하는 너비로 설정
  padding: 0 15px;
`

const SearchTerm = styled(animated.div)`
  position: absolute;
  width: 100%;
  text-align: left;
  font-size: 1.1rem;
  font-weight: bold;
`

const DropdownMenu = styled.div`
  position: absolute;
  top: 38px; // 컨테이너 바로 아래에 위치하게 설정
  right: 0;
  width: auto;
  background: white;
  color: black;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0px 5px 10px rgba(0, 0, 0, 0.4);
  z-index: 10;
  display: flex;
  flex-direction: row;
  padding: 5px;
  font-size: 0.8rem;
  justify-content: center;
`

const DropdownItem = styled.div`
  padding: 3px 3px 3px 6px;
  min-width: 100px;
  overflow-x: unset;
  font-weight: 500;

  width: auto;
  &:hover {
    background: #f0f0f0;
    cursor: pointer;
  }
  &:active {
    background: #d9d9d9;
  }
`
const DropdownItemCom = styled.div`
  padding: 3px 3px 3px 6px;
  min-width: 150px;
  overflow-x: unset;
  font-weight: 500;

  width: auto;
  &:hover {
    background: #f0f0f0;
    cursor: pointer;
  }
  &:active {
    background: #d9d9d9;
  }
`
const List = styled.div`
  display: flex;
  flex-direction: column;
`
const DropdownTitle = styled.div`
  font-weight: 600;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
`
const ListTitle = styled.div`
  font-weight: 600;
  font-size: 0.9rem;
  padding: 5px;
  border-bottom: 1px solid #efefef;
`
const ListContainer = styled.div`
  display: flex;
  flex-direction: row;
`
const ColDiv = styled.div`
  display: flex;
  flex-direction: column;
`
const Header = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  //align-items: center;
  padding: 5px;
  border-radius: 5px 5px 0 0;
  background-color: #236cff;
  color: #ffffff;
  @media only screen and (max-width: 992px) {
    font-size: 0.9rem;
  }
`
// const SmallContent = styled.div`
//   font-weight: 500;
//   font-size: 0.8rem;
//   margin: 0 10px 5px;
//   display: flex;
//   justify-content: end;
//   align-items: end;
// `

// 실시간 검색어 순위 데이터 정의
interface RankingData {
  name: string
  count: number
}

interface RankingSaveData {
  districtRankings: RankingData[]
  administrationRankings: RankingData[]
  commercialRankings: RankingData[]
  serviceRankings: RankingData[]
}
interface RankingsResponse {
  dataHeader: {
    resultCode: null
    resultMessage: number
    successCode: number
  }
  dataBody: {
    districtRankings: RankingData[]
    administrationRankings: RankingData[]
    commercialRankings: RankingData[]
    serviceRankings: RankingData[]
  }
}

const { VITE_REACT_API_URL } = import.meta.env

type RealTimeSearchTermsPropsType = {
  isHovered: boolean
  isClicked: boolean
}
const RealTimeSearchTerms = (props: RealTimeSearchTermsPropsType) => {
  const { isHovered, isClicked } = props
  const [index, setIndex] = useState(0)
  const [realTimeData, setRealTimeData] = useState<
    RankingSaveData | undefined
  >()
  const navigate = useNavigate()

  useEffect(() => {
    // 1. 초기 백엔드 서버로부터 SSE 스트림에 연결을 하기 위한 초기 설정
    const eventSource: EventSource = new EventSource(
      `${VITE_REACT_API_URL}/sse/subscribe`,
    )

    // 2. 초기 데이터 받기 용도 (SSE 연결되면 바로 보내주는 데이터)
    eventSource.onmessage = (event: MessageEvent) => {
      const data: RankingsResponse = JSON.parse(event.data)
      if (data) {
        setRealTimeData(data.dataBody)
      }
      // 데이터 아예 없을 때 더미 만들어서 넣으면 될 것 같습니다
      // console.log('Received general update:', data)
    }

    // 3. 그 이후로 실시간 검색어 순위가 5분마다 갱신된 데이터 보내줌
    // 'ranking-update' 이벤트 이름으로 백엔드 서버에서 발송한 순위 업데이트 이벤트 처리
    eventSource.addEventListener('ranking-update', (event: MessageEvent) => {
      const data: RankingsResponse = JSON.parse(event.data)
      if (data) {
        setRealTimeData(data.dataBody)
      }
      // console.log('Specific ranking update:', data)
    })
    // 4. 예외 처리 및 연결 종료
    // 클라이언트가 페이지를 나가거나 새로고침할 경우, 연결은 자동으로 종료
    eventSource.onerror = (error: Event) => {
      console.error('EventSource failed:', error)
      eventSource.close()
    }
  }, [])

  // 10위까지로 자르기 위한 로직
  useEffect(() => {
    const interval = setInterval(() => {
      if (realTimeData && realTimeData.commercialRankings.length > 0) {
        const dataLength = realTimeData.commercialRankings.length
        const sliceLength = dataLength > 10 ? 10 : dataLength

        setIndex(prevIndex => (prevIndex + 1) % sliceLength)
      }
    }, 2000)

    return () => clearInterval(interval)
  }, [realTimeData])

  const transitions = useTransition(index, {
    key: index,
    from: { opacity: 0, transform: 'translate3d(0,20px,0)' },
    enter: { opacity: 1, transform: 'translate3d(0,0px,0)' },
    leave: { opacity: 0, transform: 'translate3d(0,-20px,0)' },
    config: { duration: 500 },
  })

  let slicedData: RankingData[] = []
  if (realTimeData) {
    if (realTimeData.commercialRankings.length > 10) {
      slicedData = realTimeData.commercialRankings.slice(0, 10)
    } else {
      slicedData = realTimeData.commercialRankings
    }
  }

  // 저장버튼 누를 때 필요한 선택한 구, 동 코드 가져오기 위한 store 호출
  const { setSelectedGoo, setSelectedDong, setSelectedCommercial } =
    useSelectPlaceStore(state => ({
      setSelectedGoo: state.setSelectedGoo,
      setSelectedDong: state.setSelectedDong,
      setSelectedCommercial: state.setSelectedCommercial,
    }))

  const [koreanData, setKoreanData] = useState<string>('')
  const [koreanType, setKoreanType] = useState<string>('')

  const { data: fetchKoreaData } = useQuery({
    queryKey: [fetchKorean, koreanData, koreanType],
    queryFn: () => fetchKorean(koreanData, koreanType),
    enabled: !!koreanData || !!koreanType,
    gcTime: 0,
  })

  const handleClickItem = useCallback(
    (koreanName: string, district: string) => {
      setKoreanData(koreanName)
      setKoreanType(district)
    },
    [],
  )

  useEffect(() => {
    if (fetchKoreaData?.dataBody?.commercialCode) {
      navigate('/analysis')
      setTimeout(() => {
        setSelectedCommercial({
          name: fetchKoreaData?.dataBody?.commercialCodeName,
          code: fetchKoreaData?.dataBody?.commercialCode,
        })
        setSelectedDong({
          name: fetchKoreaData?.dataBody.administrationCodeName,
          code: fetchKoreaData?.dataBody.administrationCode,
        })
        setSelectedGoo({
          name: fetchKoreaData?.dataBody.districtCodeName,
          code: fetchKoreaData?.dataBody.districtCode,
        })
        setKoreanData('')
        setKoreanType('')
      }, 500)
    } else if (fetchKoreaData?.dataBody?.administrationCode) {
      navigate('/analysis')
      setTimeout(() => {
        setSelectedCommercial({
          name: '자치구',
          code: 0,
        })
        setSelectedDong({
          name: fetchKoreaData?.dataBody.administrationCodeName,
          code: fetchKoreaData?.dataBody.administrationCode,
        })
        setSelectedGoo({
          name: fetchKoreaData?.dataBody.districtCodeName,
          code: fetchKoreaData?.dataBody.districtCode,
        })
        setKoreanData('')
        setKoreanType('')
      }, 500)
    } else if (fetchKoreaData?.dataBody?.districtCode) {
      navigate('/analysis')
      setTimeout(() => {
        setSelectedCommercial({
          name: '자치구',
          code: 0,
        })
        setSelectedDong({
          name: '행정동',
          code: 0,
        })
        setSelectedGoo({
          name: fetchKoreaData?.dataBody.districtCodeName,
          code: fetchKoreaData?.dataBody.districtCode,
        })
        setKoreanData('')
        setKoreanType('')
      }, 500)
    }
  }, [
    fetchKoreaData,
    navigate,
    setSelectedCommercial,
    setSelectedDong,
    setSelectedGoo,
  ])

  return (
    <div>
      {slicedData.length > 0 ? (
        <Container>
          {transitions((style, i) => (
            <SearchTerm
              style={style}
            >{`${i + 1}. ${slicedData[i].name}`}</SearchTerm>
          ))}
          {isClicked ||
            (isHovered && (
              <DropdownMenu>
                <ColDiv>
                  <Header>
                    <DropdownTitle>5분 주기 실시간 검색 순위</DropdownTitle>
                    {/* <SmallContent>5분 주기 갱신</SmallContent> */}
                  </Header>
                  <ListContainer>
                    <List>
                      <ListTitle>인기 자치구</ListTitle>
                      {realTimeData?.districtRankings
                        .slice(0, 10)
                        .map((item, i) => {
                          return (
                            <DropdownItem
                              key={i}
                              onClick={() => {
                                handleClickItem(item.name, 'district')
                              }}
                            >{`${i + 1}. ${item.name}`}</DropdownItem>
                          )
                        })}
                    </List>
                    <List>
                      <ListTitle>인기 행정동</ListTitle>
                      {realTimeData?.administrationRankings
                        .slice(0, 10)
                        .map((item, i) => (
                          <DropdownItem
                            key={i}
                            onClick={() => {
                              handleClickItem(item.name, 'administration')
                            }}
                          >{`${i + 1}. ${item.name}`}</DropdownItem>
                        ))}
                    </List>
                    <List>
                      <ListTitle>인기 상권</ListTitle>
                      {realTimeData?.commercialRankings
                        .slice(0, 10)
                        .map((item, i) => (
                          <DropdownItemCom
                            key={i}
                            onClick={() => {
                              handleClickItem(item.name, 'commercial ')
                            }}
                          >{`${i + 1}. ${item.name}`}</DropdownItemCom>
                        ))}
                    </List>
                    <List>
                      <ListTitle>인기 업종</ListTitle>
                      {realTimeData?.serviceRankings
                        .slice(0, 10)
                        .map((item, i) => (
                          <DropdownItem
                            key={i}
                          >{`${i + 1}. ${item.name}`}</DropdownItem>
                        ))}
                    </List>
                  </ListContainer>
                </ColDiv>
              </DropdownMenu>
            ))}
        </Container>
      ) : null}
    </div>
  )
}

export default RealTimeSearchTerms
