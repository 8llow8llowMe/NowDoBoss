import styled, { keyframes } from 'styled-components'

export const MainContainer = styled.div`
  scroll-behavior: smooth;
  @media only screen and (max-width: 992px) {
    //
  }
`
// 페이지 별 기본 컨테이너 div
export const Container = styled.div`
  width: calc(100vw - 5px);
  //height: calc(100vh - 70px);
  height: calc(100vh);
  display: flex;

  margin: auto;
  @media only screen and (max-width: 992px) {
    height: 120vh;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
`

// 내용이 들어갈 div
export const Content = styled.div`
  height: 100%;
  margin: 0 10%;
  //background-color: #d9d9d9;
  width: calc(80% - 5px);
  display: flex;
  flex-direction: row;
  justify-content: space-between;

  @media only screen and (max-width: 992px) {
    margin: 10px 0;
    width: calc(100vw - 5px);
    flex-direction: column-reverse;
    justify-content: center;
  }
`

// 글로 소개하는 부분
export const Text = styled.div`
  width: 50%;
  //background-color: #ff7070;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 40px;

  @media only screen and (max-width: 992px) {
    width: calc(100% - 5px);
    padding: 0;
    align-items: center;
  }
`

// 파란색 부가 설명
export const BlueText = styled.div`
  color: #336dd3;
  margin-bottom: 12px;
  font-size: 1.25rem;

  @media only screen and (max-width: 600px) {
    font-size: 1rem;
  }
`

// 제목
export const Title = styled.div`
  color: #191f28;
  margin-bottom: 16px;
  font-size: 2.5rem;
  font-weight: 700;
  word-break: keep-all;
  line-height: 140%;

  @media only screen and (max-width: 992px) {
    font-size: 2rem;
  }
  @media only screen and (max-width: 700px) {
    font-size: 2rem;
  }
`

// 설명
export const TextContent = styled.div`
  color: #606d85;
  font-size: 1.5rem;
  font-weight: 600;
  line-height: 160%;
  margin-bottom: 40px;

  @media only screen and (max-width: 600px) {
    font-size: 1.2rem;
  }
`

// 바로가기 버튼
export const BannerArrow = styled.div`
  @media only screen and (max-width: 600px) {
    scale: 0.9;
  }
`

// 바로가기 버튼
export const GoButton = styled.div`
  font-weight: 600;
  font-size: 1.5rem;
  color: #333;
  display: flex;

  &:hover {
    cursor: pointer;
    // BannerArrow에만 호버 효과 적용

    ${BannerArrow} {
      transform: translateX(15px);
      transition: transform 0.3s ease;
    }
  }

  @media only screen and (max-width: 600px) {
    font-size: 1.2rem;
  }
`

export const CardContent = styled.div`
  display: flex;
  justify-content: center;
  font-weight: 600;
  font-size: 2rem;
  margin: 5px 0;
  color: #4a4a4a;
`

export const Recommend = styled.div`
  opacity: 0;
  transform: translateY(100px);
  transition:
    opacity 1s ease-out,
    transform 1s ease-out;

  &.visible {
    opacity: 1;
    transform: translateY(0);
    align-items: center;
  }

  @media only screen and (max-width: 992px) {
    height: auto;
  }
  @media only screen and (max-width: 500px) {
    height: auto;
  }
`

// moreService
export const EctContainer = styled.div`
  width: calc(100vw - 5px);
  height: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: auto;
  padding-top: 80px;
  background-color: #f0f5ff;
  @media only screen and (max-width: 992px) {
    //
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
`

// 내용이 들어갈 div
export const EctContent = styled.div`
  height: 100%;
  margin: 0 10%;
  //background-color: #d9d9d9;
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  @media only screen and (max-width: 992px) {
    flex-direction: column;
    justify-content: center;
  }
`

// 글로 소개하는 부분
export const EctText = styled.div`
  width: 70%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 0 40px;
`

// 설명
export const EctTextContent = styled.div`
  color: #606d85;
  font-size: 1.4rem;
  font-weight: 600;
  line-height: 160%;
  margin-bottom: 40px;
  text-align: center;

  @media only screen and (max-width: 1200px) {
    font-size: 1.2rem;
  }
  @media only screen and (max-width: 992px) {
    font-size: 1rem;
  }
`

export const EctCardContainer = styled.div`
  width: calc(100% - 5px);
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  background-color: #f0f5ff;
  padding: 50px 40px 100px 40px;
  gap: 50px;

  @media only screen and (max-width: 992px) {
    flex-direction: column;
    align-items: center;
  }
`
const bounce = keyframes`
  0% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0); }
`

export const EctCard = styled.div<{ $isup: boolean }>`
  width: 23%;
  min-width: 300px;
  height: 400px;
  background-color: white;
  margin-top: ${props => (props.$isup ? 0 : `60px`)};
  border-radius: 20px;
  padding: 10px;
  box-shadow: 0 0 30px 5px #d2def8;
  animation: ${bounce} 2s infinite;

  @media only screen and (max-width: 992px) {
    width: 70%;
    margin: 20px 0;
  }
`

export const EctCardContent = styled.div`
  width: calc(100% - 5px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding-top: 30px;
`

export const EctImgIcon = styled.img`
  width: 150px;
`

export const CardTitle = styled.div`
  color: #191f28;
  margin: 10px 0;
  font-size: 1.5rem;
  font-weight: 700;
  word-break: keep-all;
  line-height: 140%;
`

export const CardSubTitle = styled.div`
  width: 80%;
  color: #606d85;
  font-size: 17px;
  text-align: center;
  margin-top: 10px;
  font-weight: 500;
`
