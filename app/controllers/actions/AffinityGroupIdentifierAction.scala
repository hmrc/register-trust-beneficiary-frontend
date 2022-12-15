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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core.AffinityGroup.{Agent, Organisation}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import utils.Session

import scala.concurrent.{ExecutionContext, Future}

class AffinityGroupIdentifierAction[A] @Inject()(action: Action[A],
                                                 trustsAuthFunctions: TrustsAuthorisedFunctions,
                                                 config: FrontendAppConfig
                                                ) extends Action[A] with Logging  {
  private def authoriseAgent(request : Request[A],
                             enrolments : Enrolments,
                             internalId : String,
                             action: Action[A]
                            ) = {
    val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    def redirectToCreateAgentServicesAccount(reason: String): Future[Result] = {
      logger.info(logMessage(methodName = "authoriseAgent", sessionId = Session.id(hc), message = s"Agent services account required - $reason"))
      Future.successful(Redirect(config.createAgentServicesAccountUrl))
    }

    val hmrcAgentEnrolmentKey = "HMRC-AS-AGENT"
    val arnIdentifier = "AgentReferenceNumber"

    case class AgentIdentifier(enrolment: Enrolment, arn: String)

    val e = for {
      enrolment   <- enrolments.getEnrolment(hmrcAgentEnrolmentKey)
      identifier  <- enrolment.getIdentifier(arnIdentifier)
      _           <- if(identifier.value.nonEmpty) Some(identifier) else None
    } yield AgentIdentifier(enrolment, identifier.value)

    e.fold {
      redirectToCreateAgentServicesAccount("Agent not enrolled for HMRC-AS-AGENT")
    }{ x =>
      action(IdentifierRequest(request, internalId, AffinityGroup.Agent, enrolments, Some(x.arn)))
    }
  }

  private def authoriseOrg(request : Request[A],
                           enrolments : Enrolments,
                           internalId : String,
                           action: Action[A]
                          ): Future[Result] = {

    val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val continueWithoutEnrolment =
      action(IdentifierRequest(request, internalId, AffinityGroup.Organisation, enrolments))

    val taxEnrolment = "HMRC-TERS-ORG"
    val taxIdentifier = "SAUTR"
    val nonTaxEnrolment = "HMRC-TERSNT-ORG"
    val nonTaxIdentifier = "URN"

    val enrolment: Option[Enrolment] = for {
      enrolment <- enrolments.getEnrolment(taxEnrolment).orElse(enrolments.getEnrolment(nonTaxEnrolment))
      id        <- enrolment.getIdentifier(taxIdentifier).orElse(enrolment.getIdentifier(nonTaxIdentifier))
      _         <- if(id.value.nonEmpty) Some(id) else None
    } yield enrolment

    enrolment.fold {
      logger.info(logMessage(
        methodName = "authoriseOrg",
        sessionId = Session.id(hc),
        message = "user is not enrolled for Trusts, continuing to register online"
      ))
      continueWithoutEnrolment
    } {
      x =>
        logger.info(logMessage(
          methodName = "authoriseOrg",
          sessionId = Session.id(hc),
          message = s"user is already enrolled with ${x.key}, redirecting to maintain"
        ))
        Future.successful(Redirect(config.maintainATrustFrontendUrl))
    }

  }


  def apply(request: Request[A]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val retrievals = Retrievals.internalId and
      Retrievals.affinityGroup and
      Retrievals.allEnrolments

    trustsAuthFunctions.authorised().retrieve(retrievals) {
      case Some(internalId) ~ Some(Agent) ~ enrolments =>
        authoriseAgent(request, enrolments, internalId, action)
      case Some(internalId) ~ Some(Organisation) ~ enrolments =>
        authoriseOrg(request, enrolments, internalId, action)
      case Some(_) ~ _ ~ _ =>
        logger.info(logMessage(methodName = "apply", sessionId = Session.id(hc), message = "Unauthorised due to affinityGroup being Individual"))
        Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad))
      case _ =>
        logger.warn(logMessage(methodName = "apply", sessionId = Session.id(hc), message = "Unable to retrieve internal id"))
        throw new UnauthorizedException("Unable to retrieve internal Id")
    } recover trustsAuthFunctions.recoverFromAuthorisation
  }

  override def parser: BodyParser[A] = action.parser
  override implicit def executionContext: ExecutionContext = action.executionContext

  private def logMessage(methodName: String, sessionId:String, message: String): String =
    s"[AffinityGroupIdentifierAction][$methodName][Session ID: $sessionId] $message"

}
