import styled from 'styled-components'

const Container = styled.div`
  height: 20vh;
  background-color: #ffffff;
  color: #606d85;
  width: 100%;
  padding: 2rem 8vw;
  font-size: 0.9rem;
  font-weight: 400;
  display: flex;
  justify-content: space-between;
  align-items: center;
  display: flex;
  flex-direction: row;

  @media (max-width: 992px) {
    font-size: 0.8rem;
    padding: 2rem 5vw;
  }
  @media (max-width: 648px) {
    height: 25vh;
    padding: 5vh 10vw;
    flex-direction: column;
    align-items: center;
    text-align: left;
    width: 100%;
  }
`

const Div = styled.div`
  display: flex;
  flex-direction: column;
  gap: 6px;

  @media (max-width: 648px) {
    width: 100%;
    height: 7.5vh;
    justify-content: center;
  }
`

const LinkText = styled.a`
  text-decoration: none;
  color: #606d85;
  &:hover {
    text-decoration-line: underline;
  }
`

const FooterContainer = () => {
  return (
    <Container>
      <Div>
        Copyright © 2025 팔로팔로미. All Rights Reserved.
        <br />
        <LinkText href="mailto:nowdoboss@gmail.com">
          이메일 nowdoboss@gmail.com
        </LinkText>
      </Div>
      <Div>
        <LinkText href="https://rhinestone-beechnut-5a9.notion.site/8llow8llowme-18fd6b9f531080d98adceacf6023155a">
          팀 소개
        </LinkText>
        {/* <LinkText>서비스 피드백하기</LinkText> */}
        <LinkText href="https://github.com/8llow8llowMe/NowDoBoss">
          Github ⭐️ 서비스 운영과 발전에 큰 도움이 됩니다.
        </LinkText>
      </Div>
    </Container>
  )
}

export default FooterContainer
