import styled, { keyframes } from 'styled-components'

const infiniteAnimation1 = keyframes`
    0% {
        transform: translateY(0%);
    }
    50% {
        transform: translateY(-100%);
    }
    50.1% {
        transform: translateY(100%);
    }
    100% {
        transform: translateY(0%);
    }
`

const infiniteAnimation2 = keyframes`
    0% {
        transform: translateY(0%);
    }
    100% {
        transform: translateY(-200%);
    }
`

const infiniteAnimation5 = keyframes`
    0% {
        transform: translateX(0%);
    }
    50% {
        transform: translateX(-100%);
    }
    50.1% {
        transform: translateX(100%);
    }
    100% {
        transform: translateX(0%);
    }
`

const infiniteAnimation6 = keyframes`
    0% {
        transform: translateX(0%);
    }
    100% {
        transform: translateX(-200%);
    }
`

export const Container = styled.div`
  display: flex;
  flex-direction: column;
`

export const Main = styled.div`
  height: calc(100vh);
  position: relative;
  display: flex;
  padding: 3% 0 3% 2%;
  background-color: #fcfdff;
  align-items: center;

  @media (max-width: 1200px) {
    flex-direction: column;
    height: auto;

    padding: 5%;
  }
`

export const TextDiv = styled.div`
  box-sizing: border-box;
  width: 50%;
  display: flex;
  flex-direction: column;
  padding-left: 10%;

  @media (max-width: 1200px) {
    width: 100%;
    margin-bottom: 20vh;
    padding-left: 0;
    text-align: center;
  }

  @media (max-width: 576px) {
    margin-bottom: 10vh;
  }
`

export const TextWrap = styled.div`
  width: 100%;
  box-sizing: border-box;
`

export const BlueText = styled.div`
  color: #336dd3;
  margin-bottom: 12px;
  font-size: 1.25rem;

  @media only screen and (max-width: 600px) {
    font-size: 1rem;
  }
`

export const MainText = styled.div`
  color: #191f28;
  margin-bottom: 16px;
  font-size: 2.5rem;
  font-weight: 700;
  word-break: keep-all;
  line-height: 140%;

  @media only screen and (max-width: 600px) {
    font-size: 2rem;
  }
`

export const SubText = styled.p`
  color: #606d85;
  font-size: 1.5rem;
  font-weight: 600;
  line-height: 160%;
  margin-bottom: 40px;

  @media only screen and (max-width: 600px) {
    font-size: 1.2rem;
  }
`

export const TextStopBtn = styled.div`
  width: 17px;
  height: 17px;
  box-sizing: border-box;
  background: url('/icons/pause.png') no-repeat center center / 17px 17px;
`

export const ImgBox = styled.div`
  width: 50%;
  height: 600px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  justify-content: space-around;

  @media (max-width: 1200px) {
    width: 100%;
    padding: 0;
    justify-content: space-around;
  }

  @media (max-width: 992px) {
    flex-direction: column;
    height: auto;
    gap: 5vh;
  }
`

// 슬라이더
export const CardWrap = styled.div`
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  width: 50%;
  max-width: 350px;
  height: 100%;
  overflow-y: hidden;
  cursor: default;
  box-shadow: 0 0 42px rgb(134 171 224 / 10%);

  @media (max-width: 1200px) {
    width: 100%;
  }

  @media (max-width: 992px) {
    flex-direction: row;
    max-width: 1200px;
    height: 400px;
    overflow-x: hidden;
    gap: 2vh;
  }
`

export const OriginalCardDiv = styled.div`
  display: flex;
  flex-direction: column;
  animation: 35s linear infinite normal none running ${infiniteAnimation1};

  @media (max-width: 992px) {
    flex-direction: row;
    gap: 2vh;
    animation: 50s linear infinite normal none running ${infiniteAnimation5};
  }
`

export const CloneCardDiv = styled.div`
  display: flex;
  flex-direction: column;
  animation: 35s linear infinite normal none running ${infiniteAnimation2};

  @media (max-width: 992px) {
    flex-direction: row;
    gap: 2vh;
    animation: 50s linear infinite normal none running ${infiniteAnimation6};
  }
`

export const DescOriginalCardDiv = styled.div`
  display: flex;
  flex-direction: column;
  animation: 35s linear infinite normal none running ${infiniteAnimation1};
  animation-direction: reverse;

  @media (max-width: 992px) {
    flex-direction: row;
    gap: 2vh;
    animation: 50s linear infinite normal none running ${infiniteAnimation5};
    animation-direction: reverse;
  }
`

export const DescCloneCardDiv = styled.div`
  display: flex;
  flex-direction: column;
  animation: 35s linear infinite normal none running ${infiniteAnimation2};
  animation-direction: reverse;

  @media (max-width: 992px) {
    flex-direction: row;
    gap: 2vh;
    animation: 50s linear infinite normal none running ${infiniteAnimation6};
    animation-direction: reverse;
  }
`

export const Card = styled.div`
  width: 100%;
  min-width: 350px;
  height: 400px !important;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  justify-content: space-between;
  background-color: #fff;
  border-radius: 14px;
  box-shadow: 0 0 42px rgb(134 171 224 / 10%);
`

export const CardTextDiv = styled.div`
  padding: 32px 28px;
  box-sizing: border-box;
  border: 0;

  p {
    color: #1f65fd;
    font-weight: 400;
    font-size: 1.125rem;
    margin-bottom: 8px;
    word-break: keep-all;
  }

  h4 {
    color: #404040;
    font-weight: 700;
    font-size: 1.4rem;
    word-break: keep-all;
  }
`

export const CardImgDiv = styled.div`
  box-sizing: border-box;
  border: 0;
`

export const CardImg = styled.div`
  background: url('/images/loctation.png') no-repeat 50% 50% / contain;
  height: 200px;
  box-sizing: border-box;
`

export const Sub = styled.div`
  height: calc(100vh);
  position: relative;
  display: flex;
  padding: 5% 1%;
  background-color: #fcfdff;
`

export const SubInnerBox = styled.div`
  margin: 0 11%;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: space-between;
  flex-direction: row;
  align-items: center;
  box-sizing: border-box;

  @media only screen and (max-width: 1015px) {
    flex-direction: column;
  }
`

export const ContentDiv = styled.div`
  flex: 3;
  display: flex;
  box-sizing: border-box;
  flex-direction: column;
  transition:
    transform 0.4s ease 0s,
    opacity 0.5s ease 0s;

  h2 {
    font-weight: 700;
    font-size: 3.1rem;
    line-height: 4.1rem;
    margin-bottom: 40px;
  }

  p {
    font-weight: 400;
    font-size: 1.3rem;
    line-height: 2.15rem;
    margin-bottom: 12px;
  }

  @media only screen and (max-width: 992px) {
    font-size: 0.8rem;
    h2 {
      font-size: 3rem;
      line-height: 3.5rem;
      margin-bottom: 30px;
    }

    p {
      font-size: 1.2rem;
    }
  }
  @media only screen and (max-width: 600px) {
    font-size: 0.8rem;
    h2 {
      font-size: 2rem;
      line-height: 2.5rem;
      margin-bottom: 20px;
    }

    p {
      font-size: 1rem;
    }
  }
`

export const ImgDiv = styled.div`
  flex: 2;
  display: flex;
  box-sizing: border-box;
  flex-direction: column;
`

export const ChartImgDiv = styled.div`
  width: 100%;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    border-radius: 10px;
  }
`

export const BlueCard = styled.div`
  width: 100%;
  min-width: 350px;
  height: 400px !important;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  justify-content: space-between;
  background-color: #1f65fd;
  border-radius: 14px;
  box-shadow: 0 0 42px rgb(134 171 224 / 10%);
`

export const BlueCardTextDiv = styled.div`
  padding: 32px 28px;
  box-sizing: border-box;
  border: 0;

  p {
    color: #bbfffb;
    font-weight: 400;
    font-size: 1.125rem;
    margin-bottom: 8px;
    word-break: keep-all;
  }

  h4 {
    color: #fff;
    font-weight: 700;
    font-size: 1.4rem;
    word-break: keep-all;
  }
`

export const SpeakerCardImg = styled.div`
  background: url('/images/speaker.png') no-repeat 50% 50% / contain;
  height: 200px;
  box-sizing: border-box;
`

export const ChartCardImg = styled.div`
  background: url('/images/chart.png') no-repeat 50% 50% / contain;
  height: 200px;
  box-sizing: border-box;
`

export const Card4Img = styled.div`
  background: url('/images/awards.png') no-repeat 50% 50% / contain;
  height: 200px;
  box-sizing: boder-box;
`
