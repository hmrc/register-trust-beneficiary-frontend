/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import controllers.actions.register.{
  DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationDataRequiredActionImpl,
  RegistrationIdentifierAction
}
import controllers.actions.{FakeDraftIdRetrievalActionProvider, FakeIdentifyForRegistration}
import errors.TrustErrors
import models.{ReadOnlyUserAnswers, Status, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import repositories.RegistrationsRepository
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.{AffinityGroup, Enrolment, Enrolments}
import uk.gov.hmrc.http.HeaderCarrier

trait SpecBase
    extends PlaySpec
    with GuiceOneAppPerSuite
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with Mocked
    with FakeTrustsApp
    with Matchers
    with MockitoSugar
    with OptionValues
    with EitherValues {

  final val ENGLISH = "en"
  final val WELSH   = "cy"

  lazy val draftId: String        = "draftId"
  lazy val userInternalId: String = "internalId"
  lazy val fakeDraftId: String    = draftId

  implicit val hc: HeaderCarrier = HeaderCarrier()

  def emptyUserAnswers: UserAnswers = UserAnswers(draftId, Json.obj(), internalAuthId = userInternalId)

  lazy val fakeNavigator: FakeNavigator = new FakeNavigator()

  private def fakeDraftIdAction(userAnswers: Option[UserAnswers]): FakeDraftIdRetrievalActionProvider =
    new FakeDraftIdRetrievalActionProvider(
      draftId,
      Status.InProgress,
      userAnswers,
      mockRegistrationsRepository
    )

  protected def applicationBuilder(
    userAnswers: Option[UserAnswers] = None,
    affinityGroup: AffinityGroup = AffinityGroup.Organisation,
    enrolments: Enrolments = Enrolments(Set.empty[Enrolment]),
    navigator: Navigator = fakeNavigator,
    mockGetResult: Either[TrustErrors, Option[UserAnswers]] = Right(None),
    mockSetResult: Either[TrustErrors, Boolean] = Right(true),
    mockGetSettlorsAnswersResult: Either[TrustErrors, Option[ReadOnlyUserAnswers]] = Right(None)
  ): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[Navigator].toInstance(navigator),
        bind[RegistrationDataRequiredAction].to[RegistrationDataRequiredActionImpl],
        bind[RegistrationIdentifierAction].toInstance(
          new FakeIdentifyForRegistration(affinityGroup, frontendAppConfig)(injectedParsers, trustsAuth, enrolments)
        ),
        bind[DraftIdRetrievalActionProvider].toInstance(fakeDraftIdAction(userAnswers)),
        bind[RegistrationsRepository]
          .toInstance(mockRegistrationsRepositoryBuilder(mockGetResult, mockSetResult, mockGetSettlorsAnswersResult)),
        bind[AffinityGroup].toInstance(Organisation)
      )
      .configure(
        "play.filters.disabled" -> List("play.filters.csrf.CSRFFilter", "play.filters.csp.CSPFilter")
      )

}
