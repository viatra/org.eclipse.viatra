/*******************************************************************************
 * Copyright (c) 2014-2016 Robert Doczi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Robert Doczi - initial API and implementation
 *******************************************************************************/
#pragma once

#include <stdexcept>

namespace Viatra {
namespace Query {
namespace Util {

class not_found_error: public std::logic_error {
public:
    not_found_error(const std::string& what_arg);
};

namespace detail {

/**
 * An immutable object reference that may contain another object. Each object either contains an object or nothing.
 */
template<class T>
class OptionalReference {
public:

    virtual ~OptionalReference() {
    }

    /**
     * Checks if the optional has a value present.
     *
     * @return true if the value is present, false if the optional is empty
     */
    virtual bool present() = 0;
    /**
     * If the value is present, executes the provided function with the value as a parameter. If the value
     * is missing, it does nothing.
     *
     * @param fun the function to execute
     */
    virtual void if_present(void (*fun)(T)) = 0;
    /**
     * If the value is missing, uses the provided function to supply one. The function has no parameters, and must
     * return a value of the proper type.
     *
     * @param fun the supplier of the value
     *
     * @return either the contained or the supplied value
     */
    virtual T or_else(T (*fun)()) = 0;
    /**
     * If the value is missing, uses the provided value instead.
     *
     * @param other the other value to use
     *
     * @return either the contained or the supplied value
     */
    virtual T or_else(T other) = 0;
    /**
     * Returns the contained value. If the optional is empty, if the type of the value is a pointer it returns a
     * pointer, otherwise throws a not_found_error .
     *
     * @return the contained value
     */
    virtual T get() = 0;

private:
};

/**
 * Specialization of the Optional class for the absent case.
 *
 * This class represents an optional value that is empty, and behaves accordingly.
 */
template<class T>
class Absent: public OptionalReference<T> {
public:
    /**
     * Creates a new Absent optional.
     * @return the new Absent optional.
     */
    static Absent<T>* create();
    Absent(const Absent<T>&);
    virtual ~Absent();

    virtual bool present();
    virtual void if_present(void (*fun)(T));
    virtual T or_else(T (*fun)());
    virtual T or_else(T other);
    virtual T get();

private:
    Absent();
};

/**
 * Specialization of the Optional class for the present case.
 *
 * This class represents an optional value that containts a value, and behaves accordingly.
 */
template<class T>
class Present: public OptionalReference<T> {
public:
    /**
     * Creates a new Present optional with the value provided.
     * @param value the value to contain
     * @return the optional containing the value
     */
    static Present<T>* create(T value);
    Present(const Present<T>&);
    virtual ~Present();

    virtual bool present();
    virtual void if_present(void (*fun)(T));
    virtual T or_else(T (*fun)());
    virtual T or_else(T other);
    virtual T get();

private:
    Present(T value);

    T _value;
};

template<class T>
inline Present<T>* Present<T>::create(T value) {
    return new Present(value);
}

template<class T>
inline bool Present<T>::present() {
    return true;
}

template<class T>
inline void Present<T>::if_present(void (*fun)(T)) {
    fun(_value);
}

template<class T>
inline T Present<T>::or_else(T (*)()) {
    return _value;
}

template<class T>
inline T Present<T>::or_else(T) {
    return _value;
}

template<class T>
inline T Present<T>::get() {
    return _value;
}

template<class T>
inline Present<T>::Present(T value) :
        _value(value) {
}

template<class T>
inline Present<T>::Present(const Present<T>& present) {
}

template<class T>
inline Present<T>::~Present() {
}

template<class T>
inline Absent<T>* Absent<T>::create() {
    return new Absent<T>();
}

template<class T>
inline bool Absent<T>::present() {
    return false;
}

template<class T>
inline void Absent<T>::if_present(void (*fun)(T)) {
    return;
}

template<class T>
inline T Absent<T>::or_else(T (*fun)()) {
    return fun();
}

template<class T>
inline T Absent<T>::or_else(T other) {
    return other;
}

template<class T>
T Absent<T>::get(){
    throw new not_found_error("Tried to get the value of an empty optional.");
}

template<class T>
inline Absent<T>::Absent() {
}

template<class T>
inline Absent<T>::Absent(const Absent<T>&) {
}

template<class T>
inline Absent<T>::~Absent() {
}

}  // namespace detail

template<class T>
class Optional {
public:
        Optional(const Optional&);

        /**
         * Creates a new empty optional.
         * @return an empty optional
         */
        static Optional<T> empty();
        /**
         * Creates a new optional with the value object. Object can be either a pointer, a reference or a value.
         *
         * @param object the value the optional will contain
         *
         * @return an optional containing the specified value
         */
        static Optional<T> of(T object);

        /**
         * Checks if the optional has a value present.
         *
         * @return true if the value is present, false if the optional is empty
         */
        bool present();
        /**
         * If the value is present, executes the provided function with the value as a parameter. If the value
         * is missing, it does nothing.
         *
         * @param fun the function to execute
         */
        void if_present(void (*fun)(T));
        /**
         * If the value is missing, uses the provided function to supply one. The function has no parameters, and must
         * return a value of the proper type.
         *
         * @param fun the supplier of the value
         *
         * @return either the contained or the supplied value
         */
        T or_else(T (*fun)());
        /**
         * If the value is missing, uses the provided value instead.
         *
         * @param other the other value to use
         *
         * @return either the contained or the supplied value
         */
        T or_else(T other);
        /**
         * Returns the contained value. If the optional is empty, if the type of the value is a pointer it returns a
         * pointer, otherwise throws a not_found_error .
         *
         * @return the contained value
         */
        T get();

private:
        Optional(detail::OptionalReference<T>* ref);

        detail::OptionalReference<T>* _optionalReference;
};

inline not_found_error::not_found_error(const std::string& what_arg) :
        std::logic_error(what_arg) {
}

template<class T>
inline Optional<T> Optional<T>::empty() {
    return Optional<T>(detail::Absent<T>::create());
}

template<class T>
inline Optional<T> Optional<T>::of(T object) {
    return Optional<T>(detail::Present<T>::create(object));
}

template<class T>
inline bool Optional<T>::present() {
    return _optionalReference->present();
}

template<class T>
inline void Optional<T>::if_present(void (*fun)(T)) {
    return _optionalReference->if_present(fun);
}

template<class T>
inline T Optional<T>::or_else(T (*fun)()) {
    return _optionalReference->or_else(fun);
}

template<class T>
inline T Optional<T>::or_else(T other) {
    return _optionalReference->or_else(other);
}

template<class T>
inline T Optional<T>::get() {
    return _optionalReference->get();
}

template<class T>
inline Optional<T>::Optional(const Optional& other) :
    _optionalReference(other._optionalReference){
}

template<class T>
inline Optional<T>::Optional(detail::OptionalReference<T>* ref) :
   _optionalReference(ref) {
}


}  /* namespace Util*/
}  /* namespace Query*/
}  /* namespace Viatra */
