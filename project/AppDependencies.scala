import sbt.*

object AppDependencies {

  val bootstrapVersion = "7.21.0"

  private lazy val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"             % "7.19.0-play-28",
    "uk.gov.hmrc"             %% "domain"                         % "8.3.0-play-28",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping"  % "1.13.0-play-28",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"     % bootstrapVersion,
    "org.typelevel"           %% "cats-core"                      % "2.10.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-28"   % bootstrapVersion,
    "org.jsoup"                   %  "jsoup"                    % "1.16.1",
    "org.scalatest"               %% "scalatest"                % "3.2.16",
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.16.0",
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.14",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.27.2",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2",
    "com.vladsch.flexmark"        %  "flexmark-all"             % "0.64.8"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
