import { Link, type LinkProps } from 'react-router-dom';

import styles from './Button.module.css';

type ButtonLinkVariant = 'primary' | 'secondary';

interface ButtonLinkProps extends LinkProps {
  variant?: ButtonLinkVariant;
}

export function ButtonLink({ variant = 'primary', className, ...props }: ButtonLinkProps) {
  const classes = [styles.button, styles[variant], styles.md, className].filter(Boolean).join(' ');
  return <Link className={classes} {...props} />;
}
