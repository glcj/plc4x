/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package utils

import (
	"fmt"
	"time"
)

// ErrorIdentify is an interface defining the inline interface defined in errors.Is(err, target error) bool (wrap.go)
type ErrorIdentify interface {
	Is(target error) bool
}

type ParseAssertError struct {
	Message string
	Err     error // TODO: make available as root cause
}

func (e ParseAssertError) Error() string {
	return e.Message
}

func (e ParseAssertError) Is(target error) bool {
	_, ok := target.(ParseAssertError)
	return ok
}

type ParseValidationError struct {
	Message string
}

func (e ParseValidationError) Error() string {
	return e.Message
}

func (e ParseValidationError) Is(target error) bool {
	_, ok := target.(ParseValidationError)
	return ok
}

type TimeoutError struct {
	timeout time.Duration
}

func NewTimeoutError(timeout time.Duration) TimeoutError {
	return TimeoutError{timeout: timeout}
}

func (t TimeoutError) Error() string {
	return fmt.Sprintf("got timeout after %s", t.timeout)
}

func (t TimeoutError) Is(target error) bool {
	_, ok := target.(TimeoutError)
	return ok
}
