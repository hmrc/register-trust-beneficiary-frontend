import sbt.*

object AppDependencies {

  val bootstrapVersion = "10.5.0"

  private lazy val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"   %% "play-frontend-hmrc-play-30"            % "12.29.0",
    "uk.gov.hmrc"   %% "domain-play-30"                        % "11.0.0",
    "uk.gov.hmrc"   %% "play-conditional-form-mapping-play-30" % "3.4.0",
    "uk.gov.hmrc"   %% "bootstrap-frontend-play-30"            % bootstrapVersion,
    "org.typelevel" %% "cats-core"                             % "2.13.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapVersion,
    "org.scalatest"     %% "scalatest"              % "3.2.19",
    "org.scalatestplus" %% "scalacheck-1-18"        % "3.2.19.0",
    "org.wiremock"       % "wiremock-standalone"    % "3.13.2",
    "wolfendale"        %% "scalacheck-gen-regexp"  % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
